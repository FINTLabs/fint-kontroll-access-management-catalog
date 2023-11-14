package no.fintlabs.opa.mapper;

import no.fintlabs.accessassignment.repository.AccessAssignment;
import no.fintlabs.opa.dto.AssignmentScopeDto;
import no.fintlabs.opa.dto.RoleAndScopeDto;
import no.fintlabs.opa.dto.RolesAndScopesDto;
import no.fintlabs.user.repository.AccessUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RolesAndScopesMapper {

    public static RolesAndScopesDto mapAccessUserToRolesAndScopesDto(AccessUser user) {
        Map<String, List<AssignmentScopeDto>> roleToScopes = new HashMap<>();

        for (AccessAssignment assignment : user.getAccessAssignments()) {
            String roleId = assignment.getAccessAssignmentId().getAccessRoleId();
            AssignmentScopeDto scopeDto = AssignmentScopeMapper.mapAccessAssignmentToAssignmentScopeDto(assignment);

            roleToScopes
                    .computeIfAbsent(roleId, k -> new ArrayList<>())
                    .add(scopeDto);
        }

        List<RoleAndScopeDto> roleAndScopeDTOs = roleToScopes.entrySet().stream()
                .map(entry -> new RoleAndScopeDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return new RolesAndScopesDto(roleAndScopeDTOs);
    }
}
