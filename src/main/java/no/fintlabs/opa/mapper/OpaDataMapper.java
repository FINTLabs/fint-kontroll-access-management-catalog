package no.fintlabs.opa.mapper;

import no.fintlabs.accesspermission.AccessPermission;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.opa.dto.OpaDto;
import no.fintlabs.user.repository.AccessUser;

import java.util.List;

public class OpaDataMapper {

    public static OpaDto mapDataToOpaDto(List<AccessUser> users, List<AccessPermission> permissions, List<AccessRole> roles) {
        OpaDto opaDto = new OpaDto();

        opaDto.setUser_assignments(AssignmentsMapper.mapUserAssignments(users));
        opaDto.setRole_authorizations(RoleAuthorizationMapper.toRoleAuthorizations(permissions));
        opaDto.setRoles(RolesMapper.toRoles(roles));

        return opaDto;
    }
}
