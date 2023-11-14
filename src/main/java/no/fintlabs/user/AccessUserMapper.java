package no.fintlabs.user;

import no.fintlabs.accessassignment.AccessAssignment;
import no.fintlabs.orgunit.OrgUnitDto;
import no.fintlabs.orgunit.OrgUnitMapper;
import no.fintlabs.scope.ScopeOrgUnit;
import no.fintlabs.user.dto.AccessUserDto;
import no.fintlabs.user.dto.AccessUserScopesDto;
import no.fintlabs.user.dto.UserRoleDto;
import no.fintlabs.user.repository.AccessUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccessUserMapper {

    public static AccessUserDto toDto(AccessUser accessUser) {
        final Map<String, UserRoleDto> roles = new HashMap<>();

        for (final AccessAssignment accessAssignment : accessUser.getAccessAssignments()) {
            final String roleId = accessAssignment.getAccessAssignmentId().getAccessRoleId();
            final String roleName = accessAssignment.getAccessRole().getName();

            UserRoleDto roleScopesDto = roles.computeIfAbsent(roleId, k -> createRoleScopesDto(roleId, roleName));
            roleScopesDto.scopes().add(mapUserScopes(accessAssignment));
        }

        return new AccessUserDto(accessUser.getResourceId(), accessUser.getUserId(), accessUser.getUserName(), accessUser.getFirstName(),
                                 accessUser.getLastName(), new ArrayList<>(roles.values()));
    }

    private static UserRoleDto createRoleScopesDto(String roleId, String roleName) {
        return new UserRoleDto(roleId, roleName, new ArrayList<>());
    }

    private static AccessUserScopesDto mapUserScopes(AccessAssignment accessAssignment) {
        return new AccessUserScopesDto(accessAssignment.getAccessAssignmentId().getScopeId(),
                                       accessAssignment.getScope().getObjectType(),
                                       mapOrgUnits(accessAssignment));
    }

    private static List<OrgUnitDto> mapOrgUnits(AccessAssignment accessAssignment) {
        return accessAssignment.getScope().getScopeOrgUnits().stream()
                .map(ScopeOrgUnit::getOrgUnit)
                .map(OrgUnitMapper::toDto)
                .collect(Collectors.toList());
    }
}
