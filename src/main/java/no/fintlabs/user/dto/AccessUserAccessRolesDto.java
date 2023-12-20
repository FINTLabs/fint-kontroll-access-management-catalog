package no.fintlabs.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AccessUserAccessRolesDto {

    private String accessRoleId;
    private String accessRoleName;
    private List<AccessUserOrgUnitDto> orgUnits;
}
