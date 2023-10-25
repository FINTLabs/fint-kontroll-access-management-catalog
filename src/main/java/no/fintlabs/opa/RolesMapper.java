package no.fintlabs.opa;

import no.fintlabs.accessrole.AccessRole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RolesMapper {
    public static RoleDto toDto(AccessRole accessRole) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(accessRole.getAccessRoleId());
        return roleDto;
    }

    public static Map<String, List<RoleDto>> toRoles(List<AccessRole> roles) {
        Map<String, List<RoleDto>> map = new HashMap<>();

        roles.stream()
                .collect(Collectors.groupingBy(AccessRole::getName))
                .forEach((roleId, roleList) -> {
                    List<RoleDto> rolesList = roleList.stream()
                            .map(RolesMapper::toDto)
                            .collect(Collectors.toList());
                    map.put(roleId, rolesList);
                });

        return map;
    }
}
