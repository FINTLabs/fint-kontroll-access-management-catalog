package no.fintlabs.user;

import no.fintlabs.accessrole.AccessRole;

public class AccessRoleMother {
    public static AccessRole createDefaultAccessRole() {
        return AccessRole.builder()
                .accessRoleId("ata")
                .name("Applikasjonstilgangsadministrator")
                .build();
    }

    public static AccessRole createAccessRole(String accessRoleId, String name) {
        return AccessRole.builder()
                .accessRoleId(accessRoleId)
                .name(name)
                .build();
    }
}
