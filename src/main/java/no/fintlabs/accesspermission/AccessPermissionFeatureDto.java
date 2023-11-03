package no.fintlabs.accesspermission;

import lombok.Data;

import java.util.List;

@Data
public class AccessPermissionFeatureDto {

    private Long featureId;
    private String featureName;
    private List<String> operations;
}
