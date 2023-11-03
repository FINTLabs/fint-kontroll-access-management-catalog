package no.fintlabs.accesspermission;

import lombok.Data;

import java.util.List;

@Data
public class AccessRolePermissionDto {
    private String accessRoleId;
    private List<AccessPermissionFeatureDto> features;
}
