package no.fintlabs.accesspermission;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccessPermissionMapper {

    public static AccessPermission toAccessPermission(AccessPermissionDto accessPermissionDto) {
        return new AccessPermission(accessPermissionDto.accessRoleId(), accessPermissionDto.featureId(), accessPermissionDto.operation());
    }

    public static AccessPermissionDto toDto(AccessPermission accessPermission) {
        return new AccessPermissionDto(accessPermission.getAccessRoleId(), accessPermission.getFeatureId(),
                                       accessPermission.getOperation());
    }

    public static List<AccessRolePermissionDto> toAccessRolePermissionDtos(List<AccessPermission> accessPermissions) {
        if (accessPermissions == null || accessPermissions.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, List<AccessPermission>> groupedByAccessRole = accessPermissions.stream()
                .collect(Collectors.groupingBy(AccessPermission::getAccessRoleId));

        return groupedByAccessRole.entrySet().stream()
                .map(entry -> mapSingleAccessRole(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private static AccessRolePermissionDto mapSingleAccessRole(String accessRoleId, List<AccessPermission> permissions) {
        AccessRolePermissionDto dto = new AccessRolePermissionDto();
        dto.setAccessRoleId(accessRoleId);

        Map<Long, List<String>> featureToOperationsMap = permissions.stream()
                .collect(Collectors.groupingBy(ap -> ap.getFeature().getId(),
                                               Collectors.mapping(AccessPermission::getOperation, Collectors.toList())));

        List<AccessPermissionFeatureDto> featureDtos = featureToOperationsMap.entrySet().stream().map(entry -> {
            AccessPermissionFeatureDto featureDto = new AccessPermissionFeatureDto();
            featureDto.setFeatureId(entry.getKey());
            featureDto.setFeatureName(permissions.stream()
                                       .filter(ap -> ap.getFeature().getId().equals(entry.getKey()))
                                       .findFirst()
                                       .map(ap -> ap.getFeature().getName())
                                       .orElse(null));
            featureDto.setOperations(entry.getValue());
            return featureDto;
        }).collect(Collectors.toList());

        dto.setFeatures(featureDtos);

        return dto;
    }
}
