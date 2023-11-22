package no.fintlabs.user;

import no.fintlabs.accessassignment.repository.AccessAssignment;
import no.fintlabs.orgunit.OrgUnitDto;
import no.fintlabs.orgunit.OrgUnitMapper;
import no.fintlabs.user.dto.AccessUserDto;
import no.fintlabs.user.dto.AccessUserScopesDto;
import no.fintlabs.user.dto.UserRoleDto;
import no.fintlabs.user.repository.AccessUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccessUserMapper {

    public static AccessUserDto toDto(AccessUser accessUser) {
        final Map<String, UserRoleDto> roles = new HashMap<>();

        for (final AccessAssignment accessAssignment : accessUser.getAccessAssignments()) {

            final String roleId = accessAssignment.getAccessRole().getAccessRoleId();
            final String roleName = accessAssignment.getAccessRole().getName();

            UserRoleDto roleScopesDto = roles.computeIfAbsent(roleId, k -> createRoleScopesDto(roleId, roleName));
            roleScopesDto.scopes().addAll(mapAssignment(accessAssignment));
        }

        return new AccessUserDto(accessUser.getResourceId(), accessUser.getUserId(), accessUser.getUserName(), accessUser.getFirstName(),
                                 accessUser.getLastName(), new ArrayList<>(roles.values()));
    }

    private static UserRoleDto createRoleScopesDto(String roleId, String roleName) {
        return new UserRoleDto(roleId, roleName, new ArrayList<>());
    }

    private static List<AccessUserScopesDto> mapAssignment(AccessAssignment accessAssignment) {
        List<AccessUserScopesDto> accessUserScopesDtos = new ArrayList<>();

        accessAssignment.getScopes().forEach(scope -> {
            List<OrgUnitDto> orgUnits = OrgUnitMapper.toDto(scope.getOrgUnits());

            AccessUserScopesDto accessUserScopesDto = new AccessUserScopesDto(scope.getId(), scope.getObjectType(), orgUnits);
            accessUserScopesDtos.add(accessUserScopesDto);

        });

        return accessUserScopesDtos;
    }
}
