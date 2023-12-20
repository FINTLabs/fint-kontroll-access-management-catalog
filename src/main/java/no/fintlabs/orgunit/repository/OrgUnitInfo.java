package no.fintlabs.orgunit.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrgUnitInfo {
    private String accessRoleId;
    private String accessRoleName;
    private Long scopeId;
    private String objectType;
    private String name;
    private String orgUnitId;
}
