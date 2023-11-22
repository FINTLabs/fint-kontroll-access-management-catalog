package no.fintlabs.orgunit;

import no.fintlabs.orgunit.repository.OrgUnit;

import java.util.List;
import java.util.stream.Collectors;

public class OrgUnitMapper {

    public static OrgUnitDto toDto(OrgUnit orgUnit) {
        return new OrgUnitDto(orgUnit.getOrgUnitId(), orgUnit.getName(), orgUnit.getShortName());
    }


    public static List<OrgUnitDto> toDto(List<OrgUnit> orgUnits) {
        return orgUnits.stream()
                .map(OrgUnitMapper::toDto)
                .collect(Collectors.toList());
    }
}
