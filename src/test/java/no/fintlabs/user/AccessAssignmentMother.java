package no.fintlabs.user;

import no.fintlabs.accessassignment.AccessAssignment;
import no.fintlabs.accessassignment.AccessAssignmentId;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.orgunit.OrgUnit;
import no.fintlabs.scope.Scope;
import no.fintlabs.scope.ScopeOrgUnit;
import no.fintlabs.scope.ScopeOrgUnitId;

import java.util.List;

public class AccessAssignmentMother {

    public static AccessAssignment createDefaultAccessAssignment() {
        return AccessAssignment.builder()
                .accessAssignmentId(createAccessAssignmentId())
                .scope(createScope())
                .accessRole(createAccessRole())
                .build();
    }

    private static Scope createScope() {
        return Scope.builder()
                .id(1L)
                .objectType("orgunit")
                .scopeOrgUnits(List.of(createScopeOrgUnit("198", "OrgUnit1"), createScopeOrgUnit("153", "OrgUnit2")))
                .build();
    }

    private static AccessRole createAccessRole() {
        return AccessRole.builder()
                .accessRoleId("ata")
                .name("Applikasjonstilgangsadministrator")
                .build();
    }

    private static ScopeOrgUnit createScopeOrgUnit(String number, String OrgUnit2) {
        return ScopeOrgUnit.builder()
                .scopeOrgUnitId(createScopeOrgUnitId(number))
                .orgUnit(createOrgUnit(number, OrgUnit2))
                .build();
    }

    private static OrgUnit createOrgUnit(String number, String OrgUnit1) {
        return OrgUnit.builder()
                .orgUnitId(number)
                .name(OrgUnit1)
                .build();
    }

    private static ScopeOrgUnitId createScopeOrgUnitId(String number) {
        return ScopeOrgUnitId.builder()
                .scopeId(1L)
                .orgUnitId(number)
                .build();
    }

    private static AccessAssignmentId createAccessAssignmentId() {
        return AccessAssignmentId.builder()
                .scopeId(1L)
                .userId("1")
                .accessRoleId("ata")
                .build();
    }
}
