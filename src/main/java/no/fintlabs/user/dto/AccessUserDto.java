package no.fintlabs.user.dto;

import java.util.List;

public record AccessUserDto(String resourceId, String userId, String userName, String firstName, String lastName, List<UserRoleDto> roles) {
}
