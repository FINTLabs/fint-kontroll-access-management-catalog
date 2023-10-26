package no.fintlabs.opa;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.accessassignment.AccessAssignment;
import no.fintlabs.accesspermission.AccessPermission;
import no.fintlabs.accesspermission.AccessPermissionRepository;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.accessrole.AccessRoleRepository;
import no.fintlabs.user.AccessUser;
import no.fintlabs.user.AccessUserRepository;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
    private final AccessPermissionRepository accessPermissionRepository;
    private final AccessRoleRepository accessRoleRepository;

    @Value("${opa.jsonexport.filename}")
    private String opaFile;

    public OpaExporter(AccessUserRepository accessUserRepository, AccessPermissionRepository accessPermissionRepository, AccessRoleRepository accessRoleRepository) {
        this.accessUserRepository = accessUserRepository;
        this.accessPermissionRepository = accessPermissionRepository;
        this.accessRoleRepository = accessRoleRepository;
    }

//    @Scheduled(cron = "* * * * * ?") //Every 10 minutes
    @Scheduled(cron = "0 */10 * * * ?") //Every 10 minutes
    public void exportOpaDataToJson() throws Exception {
        log.info("Exporting datafile for OPA");
        try {
            String opaFilePath = System.getProperty("java.io.tmpdir") + "/opa/datacatalog/" + opaFile;

            List<AccessUser> users = accessUserRepository.findAll();
            List<AccessPermission> permissions = accessPermissionRepository.findAll();
            List<AccessRole> roles = accessRoleRepository.findAll();

            OpaDto opaDto = mapDataToOpaDto(users, permissions, roles);
            writeToFile(opaDto, opaFilePath);
            createOpaBundle(opaFilePath);
        } catch (Exception e) {
            log.error("Failed to export datafile for OPA", e);
            throw e;
        }
    }

    private void writeToFile(OpaDto data, String opaFilePath) throws IOException {
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

            addFileToTar(tOut, new FileInputStream(opaDatafilePath), "datacatalog/data.json");

            Resource resource = new ClassPathResource("opa/policy/auth.rego");

            try (InputStream in = resource.getInputStream()) {
                addFileToTar(tOut, in, "auth.rego");
            }
        }
    }

    private void addFileToTar(TarArchiveOutputStream tOut, InputStream in, String base) throws IOException {
        TarArchiveEntry tarEntry = new TarArchiveEntry(base);
        tarEntry.setSize(in.available()); // Set the size, important for the tar to know how much to read
        tOut.putArchiveEntry(tarEntry);
        IOUtils.copy(in, tOut);
        tOut.closeArchiveEntry();
    }

    private OpaDto mapDataToOpaDto(List<AccessUser> users, List<AccessPermission> permissions, List<AccessRole> roles) {
        OpaDto opaDto = new OpaDto();

        opaDto.setUser_assignments(mapUserAssignments(users));
        opaDto.setRole_authorizations(RoleAuthorizationMapper.toRoleAuthorizations(permissions));
        opaDto.setRoles(RolesMapper.toRoles(roles));

        return opaDto;
    }

    private Map<String, List<RolesAndScopesDto>> mapUserAssignments(List<AccessUser> users) {
        Map<String, List<RolesAndScopesDto>> userAssignments = new HashMap<>();

        for (AccessUser user : users) {
            RolesAndScopesDto rolesAndScopesWrapper = mapAccessUserToRolesAndScopesDto(user);
            userAssignments.put(user.getUserName(), Collections.singletonList(rolesAndScopesWrapper));
        }
        return userAssignments;
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
