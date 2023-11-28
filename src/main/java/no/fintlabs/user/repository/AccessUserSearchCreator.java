package no.fintlabs.user.repository;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class AccessUserSearchCreator {

    public static Specification<AccessUser> createAccessUserSearch(String name, List<String> orgUnitIds, String accessRoleId) {
        Specification<AccessUser> spec = Specification.where(null);

        if(name != null && !name.trim().isEmpty()) {
            spec = spec.and(AccessUserSpecification.nameContains(name));
        }

        if (orgUnitIds != null && !orgUnitIds.isEmpty()) {
            spec = spec.and(AccessUserSpecification.hasOrganisationUnitIds(orgUnitIds));
        }

        if (accessRoleId != null && !accessRoleId.trim().isEmpty()) {
            spec = spec.and(AccessUserSpecification.hasAccessRoleId(accessRoleId));
        }
        return spec;
    }
}
