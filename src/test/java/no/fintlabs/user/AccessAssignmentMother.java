package no.fintlabs.user;

import no.fintlabs.accessassignment.repository.AccessAssignment;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.scope.repository.Scope;
import no.fintlabs.user.repository.AccessUser;

import java.util.List;

import static no.fintlabs.user.ScopeMother.createDefaultScope;

public class AccessAssignmentMother {

    public static AccessAssignment createDefaultAccessAssignment() {
        return AccessAssignment.builder()
                .id(999L)
                .accessUser(createAccessUser())
                .scopes(List.of(createDefaultScope()))
                .accessRole(createAccessRole())
                .build();
    }

    public static AccessAssignment createAccessAssignmentWithScope(AccessUser accessUser, Scope scope, AccessRole accessRole) {
        return AccessAssignment.builder()
                .id(999L)
                .accessUser(accessUser)
                .scopes(List.of(scope))
                .accessRole(accessRole)
                .build();
    }

    public static AccessAssignment createAccessAssignmentWithScopes(AccessUser accessUser, List<Scope> scopes, AccessRole accessRole) {
        return AccessAssignment.builder()
                .id(999L)
                .accessUser(accessUser)
                .scopes(scopes)
                .accessRole(accessRole)
                .build();
    }

    private static AccessUser createAccessUser() {
        return AccessUser.builder()
                .resourceId("123")
                .userName("testuser@test.no")
                .userId("user1")
                .build();
    }

    private static AccessRole createAccessRole() {
        return AccessRole.builder()
                .accessRoleId("ata")
                .name("Applikasjonstilgangsadministrator")
                .build();
    }


}
