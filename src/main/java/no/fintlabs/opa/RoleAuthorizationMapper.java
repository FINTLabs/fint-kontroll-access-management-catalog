package no.fintlabs.opa;

import no.fintlabs.accesspermission.AccessPermission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoleAuthorizationMapper {
    public static RoleAuthorizationsDto toDto(AccessPermission permission) {
        RoleAuthorizationsDto roleAuthorization = new RoleAuthorizationsDto();
        roleAuthorization.setOperation(permission.getOperation());
        roleAuthorization.setUri(permission.getFeature().getPath());
        return roleAuthorization;
    }

    public static Map<String, List<RoleAuthorizationsDto>> toRoleAuthorizations(List<AccessPermission> permissions) {
        Map<String, List<RoleAuthorizationsDto>> map = new HashMap<>();

        permissions.stream()
                .collect(Collectors.groupingBy(AccessPermission::getAccessRoleId))
                .forEach((roleId, permissionList) -> {
                    List<RoleAuthorizationsDto> roleAuthorizationList = permissionList.stream()
                            .map(RoleAuthorizationMapper::toDto)
                            .collect(Collectors.toList());
                    map.put(roleId, roleAuthorizationList);
                });

        return map;
    }
}
