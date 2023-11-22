package no.fintlabs.user;

import no.fintlabs.orgunit.repository.OrgUnit;

public class OrgUnitMother {
    public static OrgUnit createDefaultOrgUnit() {
        return OrgUnit.builder()
                .orgUnitId("198")
                .name("OrgUnit1")
                .build();
    }

    public static OrgUnit createOrgUnit(String number, String OrgUnit1) {
        return OrgUnit.builder()
                .orgUnitId(number)
                .name(OrgUnit1)
                .build();
    }
}
