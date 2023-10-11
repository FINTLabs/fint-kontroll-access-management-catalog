package no.fintlabs.feature;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessRoleFeature {
    private String name;
    private String path;
    private String operation;
}
