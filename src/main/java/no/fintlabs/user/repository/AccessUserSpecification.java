package no.fintlabs.user.repository;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class AccessUserSpecification {

    public static Specification<AccessUser> hasOrganisationUnitIds(List<String> orgUnitIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            // Join the AccessUserOrgUnit entity
            Join<AccessUser, AccessUserOrgUnit> orgUnitJoin = root.join("accessUserOrgUnits");

            // Use the orgUnit association from AccessUserOrgUnit to get the actual orgUnitId
            Path<String> orgUnitIdPath = orgUnitJoin.get("orgUnit").get("orgUnitId");

            // Construct the 'in' clause for the search
            return orgUnitIdPath.in(orgUnitIds);
        };
    }

    public static Specification<AccessUser> nameContains(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Expression<String>
                    concatenatedName = criteriaBuilder.concat(criteriaBuilder.concat(root.get("firstName"), " "), root.get("lastName"));

            return criteriaBuilder.like(criteriaBuilder.upper(concatenatedName), "%" + name.toUpperCase() + "%");
        };
    }

    public static Specification<AccessUser> hasUserType(String userType) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("userType"), userType);
    }
}
