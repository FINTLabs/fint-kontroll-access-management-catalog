package no.fintlabs.user;

import no.fintlabs.scope.repository.Scope;
import no.fintlabs.user.repository.AccessUser;

import java.util.List;

public class AccessUserMother {
    public static AccessUser createDefaultAccessUser() {
        return AccessUser.builder()
                .resourceId("123")
                .userName("test@test.no")
                .resourceId("123")
                .build();
    }

    public static AccessUser createAccessUser(String resourceId, String userName) {
        return AccessUser.builder()
                .resourceId(resourceId)
                .userName(userName)
                .build();
    }

    public static AccessUser createAccessUserWithAssignments(String resourceId, String userName) {
        return AccessUser.builder()
                .resourceId(resourceId)
                .userName(userName)
                .accessAssignments(List.of(AccessAssignmentMother.createDefaultAccessAssignment()))
                .build();
    }

    public static AccessUser createAccessUserWithAssignmentsAndScopes(String resourceId, String userName) {
        Scope orgUnitScope = ScopeMother.createScope(1L, "orgunit", List.of(OrgUnitMother.createDefaultOrgUnit()));
        Scope userScope = ScopeMother.createScope(2L, "user", List.of(OrgUnitMother.createDefaultOrgUnit()));

        List<Scope> scopes = List.of(orgUnitScope, userScope);

        return AccessUser.builder()
                .resourceId(resourceId)
                .userName(userName)
                .accessAssignments(List.of(AccessAssignmentMother.createAccessAssignmentWithScopes(createDefaultAccessUser(), scopes, AccessRoleMother.createDefaultAccessRole())))
                .build();
    }
}
