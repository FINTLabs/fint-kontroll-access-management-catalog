package no.fintlabs.orgunit;

import no.fintlabs.orgunit.repository.OrgUnit;
import no.fintlabs.scope.repository.ScopeOrgUnit;

import java.util.List;

public class OrgUnitMapper {

    public static List<OrgUnitDto> scopeOrgUnitsToDtos(List<ScopeOrgUnit> scopeOrgUnits) {
        return scopeOrgUnits.stream()
                .map(ScopeOrgUnit::getOrgUnit)
                .map(OrgUnitMapper::toDto)
                .toList();
    }

    public static OrgUnitDto toDto(OrgUnit orgUnit) {
        return new OrgUnitDto(orgUnit.getOrgUnitId(), orgUnit.getName(), orgUnit.getShortName());
    }


}
