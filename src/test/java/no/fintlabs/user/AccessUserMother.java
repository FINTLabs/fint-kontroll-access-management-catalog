package no.fintlabs.user;

import no.fintlabs.user.repository.AccessUser;

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
}
