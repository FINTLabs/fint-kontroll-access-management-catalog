package no.fintlabs.user;

import no.fintlabs.orgunit.repository.OrgUnit;
import no.fintlabs.scope.repository.Scope;

import java.util.List;

import static no.fintlabs.user.OrgUnitMother.createOrgUnit;

public class ScopeMother {
    public static Scope createDefaultScope() {
        return Scope.builder()
                .id(1L)
                .objectType("orgunit")
                .orgUnits(List.of(createOrgUnit("198", "OrgUnit1"), createOrgUnit("153", "OrgUnit2")))
                .build();
    }

    public static Scope createScope(Long scopeId, String objectType, List<OrgUnit> orgUnits) {
        return Scope.builder()
                .id(scopeId)
                .objectType(objectType)
                .orgUnits(orgUnits)
                .build();
    }
}
