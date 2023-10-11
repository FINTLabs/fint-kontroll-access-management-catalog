package no.fintlabs.accesspermission;

public class AccessPermissionMapper {

    public static AccessPermission toAccessPermission(AccessPermissionDto accessPermissionDto) {
        return new AccessPermission(accessPermissionDto.accessRoleId(), accessPermissionDto.featureId(), accessPermissionDto.operation());
    }

    public static AccessPermissionDto toDto(AccessPermission accessPermission) {
        return new AccessPermissionDto(accessPermission.getAccessRoleId(), accessPermission.getFeatureId(), accessPermission.getOperation());
    }
}
