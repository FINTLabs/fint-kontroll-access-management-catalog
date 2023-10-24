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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Scheduled(cron = "0 */10 * * * ?") //Every 10 minutes
    public void exportOpaDataToJson() throws Exception {
        log.info("Exporting datafile for OPA");
        try {
            List<AccessUser> users = accessUserRepository.findAll();
            UserAssignmentsDto userAssignmentsDto = mapUsersToUserAssignmentsDto(users);
            writeToFile(userAssignmentsDto, opaFile);
            createOpaBundle();
        } catch (Exception e) {
            log.error("Failed to export datafile for OPA", e);
            throw e;
        }
    }

    private void writeToFile(UserAssignmentsDto data, String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(data);

        Path opaDataPath = Paths.get("src", "main", "resources", "opa", "datacatalog", filename);


        if (!Files.exists(opaDataPath.getParent())) {
            Files.createDirectories(opaDataPath.getParent());
        }

        Files.write(opaDataPath, json);
    }

    private void createOpaBundle() throws IOException {
        Path bundlePath = Paths.get("src", "main", "resources", "opabundle", "my-bundle.tar.gz");

        if (!Files.exists(bundlePath.getParent())) {
            Files.createDirectories(bundlePath.getParent());
        }

        File tarFile = new File("src/main/resources/opabundle/my-bundle.tar.gz");

        try (FileOutputStream fOut = new FileOutputStream(tarFile);
             GZIPOutputStream gzOut = new GZIPOutputStream(fOut);
             TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {

            //TODO: skriv opa json til tmp og hent derfra, får ikke tak i src når man kjører i docker

            addFileToTar(tOut, "src/main/resources/opa/datacatalog/" + opaFile, "datacatalog/data.json");
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
