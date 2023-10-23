package no.fintlabs.opa;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.accessassignment.AccessAssignment;
import no.fintlabs.user.AccessUser;
import no.fintlabs.user.AccessUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OpaExporter {
    private final AccessUserRepository accessUserRepository;

    @Value("${opa.jsonexport.path}")
    private String exportPath;

    public OpaExporter(AccessUserRepository accessUserRepository) {
        this.accessUserRepository = accessUserRepository;
    }

    @Scheduled(cron = "0 */10 * * * ?") //Every 10 minutes
    public void exportOpaDataToJson() throws Exception {
        log.info("Exporting datafile for OPA");
        try {
            List<AccessUser> users = accessUserRepository.findAll();
            UserAssignmentsDto userAssignmentsDto = mapUsersToUserAssignmentsDto(users);
            writeToFile(userAssignmentsDto, exportPath);
        } catch (Exception e) {
            log.error("Failed to export datafile for OPA", e);
            throw e;
        }
    }

    public void writeToFile(UserAssignmentsDto data, String filename) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(data);
        Files.write(Paths.get(filename), json);
    }

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



    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }
}
