package no.fintlabs.user.dto;

import java.util.List;

public record UserRoleDto(String roleId, String roleName, List<AccessUserScopesDto> scopes) {
}
