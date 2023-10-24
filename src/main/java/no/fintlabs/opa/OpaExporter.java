package no.fintlabs.opa;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.accessassignment.AccessAssignment;
import no.fintlabs.user.AccessUser;
import no.fintlabs.user.AccessUserRepository;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

@Component
@Slf4j
public class OpaExporter {
    private final AccessUserRepository accessUserRepository;

    @Value("${opa.jsonexport.filename}")
    private String opaFile;

    public OpaExporter(AccessUserRepository accessUserRepository) {
        this.accessUserRepository = accessUserRepository;
    }

    @Scheduled(cron = "* * * * * ?") //Every 10 minutes
//    @Scheduled(cron = "0 */10 * * * ?") //Every 10 minutes
    public void exportOpaDataToJson() throws Exception {
        log.info("Exporting datafile for OPA");
        try {
            String opaFilePath = System.getProperty("java.io.tmpdir") + "/opa/datacatalog/" + opaFile;

            List<AccessUser> users = accessUserRepository.findAll();
            UserAssignmentsDto userAssignmentsDto = mapUsersToUserAssignmentsDto(users);
            writeToFile(userAssignmentsDto, opaFilePath);
            createOpaBundle(opaFilePath);
        } catch (Exception e) {
            log.error("Failed to export datafile for OPA", e);
            throw e;
        }
    }

    private void writeToFile(UserAssignmentsDto data, String opaFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(data);

        log.info("Writing OPA data to file: {}", opaFilePath);

        File opaFileInTmp = new File(opaFilePath);

        if(!opaFileInTmp.getParentFile().exists()) {
            opaFileInTmp.getParentFile().mkdirs();
        }

        try(FileWriter fileWriter = new FileWriter(opaFileInTmp)) {
            fileWriter.write(new String(json));
            log.info("Wrote OPA data to file: {}", opaFileInTmp.getAbsolutePath());
        }
    }

    private void createOpaBundle(String opaDatafilePath) throws IOException {
        String tarFile = System.getProperty("java.io.tmpdir") + "/opabundle/opabundle.tar.gz";

        File opaBundlePath = new File(tarFile);

        if(!opaBundlePath.getParentFile().exists()) {
            opaBundlePath.getParentFile().mkdirs();
        }

        log.info("Writing OPA data to file: {}", tarFile);

        try (FileOutputStream fOut = new FileOutputStream(tarFile);
             GZIPOutputStream gzOut = new GZIPOutputStream(fOut);
             TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {

            //TODO: skriv opa json til tmp og hent derfra, får ikke tak i src når man kjører i docker

            addFileToTar(tOut, opaDatafilePath, "datacatalog/data.json");
            addFileToTar(tOut, "src/main/resources/opa/policy/auth.rego", "auth.rego");
        }
    }

    private void addFileToTar(TarArchiveOutputStream tOut, String path, String base) throws IOException {
        File f = new File(path);

        try (InputStream in = new FileInputStream(f)) {
            TarArchiveEntry tarEntry = new TarArchiveEntry(f, base);
            tOut.putArchiveEntry(tarEntry);
            IOUtils.copy(in, tOut);
            tOut.closeArchiveEntry();
        }
    }

    //TODO: få inn data om roller og role authorizations
    private UserAssignmentsDto mapUsersToUserAssignmentsDto(List<AccessUser> users) {
        UserAssignmentsDto userDto = new UserAssignmentsDto();
        Map<String, List<RolesAndScopesDto>> userAssignments = new HashMap<>();

        for (AccessUser user : users) {
            RolesAndScopesDto rolesAndScopesWrapper = mapAccessUserToRolesAndScopesDto(user);
            userAssignments.put(user.getUserName(), Collections.singletonList(rolesAndScopesWrapper));
        }

        userDto.setUser_assignments(userAssignments);
        return userDto;
    }

    private RolesAndScopesDto mapAccessUserToRolesAndScopesDto(AccessUser user) {
        Map<String, List<AssignmentScopeDto>> roleToScopes = new HashMap<>();

        for (AccessAssignment assignment : user.getAccessAssignments()) {
            String roleId = assignment.getAccessAssignmentId().getAccessRoleId();
            AssignmentScopeDto scopeDto = mapAccessAssignmentToAssignmentScopeDto(assignment);

            roleToScopes
                    .computeIfAbsent(roleId, k -> new ArrayList<>())
                    .add(scopeDto);
        }

        List<RoleAndScopeDto> roleAndScopeDTOs = roleToScopes.entrySet().stream()
                .map(entry -> new RoleAndScopeDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new RolesAndScopesDto(roleAndScopeDTOs);
    }

    private AssignmentScopeDto mapAccessAssignmentToAssignmentScopeDto(AccessAssignment assignment) {
        AssignmentScopeDto scopeDto = new AssignmentScopeDto();
        scopeDto.setId(assignment.getScope().getId().toString());
        scopeDto.setObjecttype(assignment.getScope().getObjectType());
        scopeDto.setOrgunits(assignment.getScope().getScopeOrgUnits().stream()
                                     .map(orgUnit -> orgUnit.getOrgUnit().getOrgUnitId())
                                     .collect(Collectors.toList()));
        return scopeDto;
    }



    public void setOpaFile(String opaFile) {
        this.opaFile = opaFile;
    }
}
