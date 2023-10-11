package no.fintlabs.accesspermission;

public record AccessPermissionDto(String accessRoleId, Long featureId, String operation) {
}
