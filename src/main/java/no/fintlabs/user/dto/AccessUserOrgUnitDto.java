package no.fintlabs.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccessUserOrgUnitDto {

    private Long scopeId;
    private String objectType;
    private String name;
}
