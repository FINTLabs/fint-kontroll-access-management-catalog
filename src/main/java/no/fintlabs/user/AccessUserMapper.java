package no.fintlabs.user;

import no.fintlabs.accessassignment.repository.AccessAssignment;
import no.fintlabs.orgunit.repository.OrgUnitInfo;
import no.fintlabs.user.dto.AccessRoleKey;
import no.fintlabs.user.dto.AccessUserAccessRolesDto;
import no.fintlabs.user.dto.AccessUserDto;
import no.fintlabs.user.dto.AccessUserOrgUnitDto;
import no.fintlabs.user.dto.AccessUserOrgUnitsResponseDto;
import no.fintlabs.user.dto.UserRoleDto;
import no.fintlabs.user.repository.AccessUser;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccessUserMapper {

    public static AccessUserDto toAccessUserDto(AccessUser accessUser) {
        final Map<String, UserRoleDto> roles = new HashMap<>();

        for (final AccessAssignment accessAssignment : accessUser.getAccessAssignments()) {

            final String roleId = accessAssignment.getAccessRole().getAccessRoleId();
            final String roleName = accessAssignment.getAccessRole().getName();

            roles.computeIfAbsent(roleId, k -> createRoleScopesDto(roleId, roleName));
        }

        return new AccessUserDto(accessUser.getResourceId(), accessUser.getUserName(), accessUser.getFirstName(),
                                 accessUser.getLastName(), new ArrayList<>(roles.values()));
    }

    public static Map<String, Object> toAccessUserDtos(Page<AccessUser> accessUsers) {
        Map<String, Object> response = new HashMap<>();

        response.put("users", accessUsers.getContent()
                .stream()
                .map(AccessUserMapper::toAccessUserDto)
                .collect(Collectors.toList()));
        response.put("currentPage", accessUsers.getNumber());
        response.put("totalItems", accessUsers.getTotalElements());
        response.put("totalPages", accessUsers.getTotalPages());
        return response;
    }

    public static AccessUserOrgUnitsResponseDto toAccessUserOrgUnitDto(Page<OrgUnitInfo> orgUnits) {
        Map<AccessRoleKey, List<AccessUserOrgUnitDto>> groupedByAccessRoleId = orgUnits.getContent().stream()
                .collect(Collectors.groupingBy(
                        orgUnitInfo -> new AccessRoleKey(orgUnitInfo.getAccessRoleId(), orgUnitInfo.getAccessRoleName()),
                        Collectors.mapping(AccessUserMapper::createOrgUnitData, Collectors.toList())
                ));

        List<AccessUserAccessRolesDto> groupedResponse = new ArrayList<>();
        groupedByAccessRoleId.forEach((key, value) -> groupedResponse.add(new AccessUserAccessRolesDto(key.accessRoleId(),
                                                                                                       key.accessRoleName(), value)));

        return new AccessUserOrgUnitsResponseDto(orgUnits.getTotalElements(), orgUnits.getTotalPages(), orgUnits.getNumber(), groupedResponse);
    }

    private static UserRoleDto createRoleScopesDto(String roleId, String roleName) {
        return new UserRoleDto(roleId, roleName);
    }

    private static AccessUserOrgUnitDto createOrgUnitData(OrgUnitInfo orgUnitInfo) {
        return new AccessUserOrgUnitDto(orgUnitInfo.getScopeId(), orgUnitInfo.getObjectType(), orgUnitInfo.getName(), orgUnitInfo.getOrgUnitId());
    }
}
