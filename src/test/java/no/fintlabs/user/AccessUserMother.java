package no.fintlabs.user;

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
}
