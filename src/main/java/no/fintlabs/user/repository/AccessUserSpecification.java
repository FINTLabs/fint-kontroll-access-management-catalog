package no.fintlabs.user.repository;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import no.fintlabs.accessassignment.repository.AccessAssignment;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class AccessUserSpecification {

    public static Specification<AccessUser> hasOrganisationUnitIds(List<String> orgUnitIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            // Join the AccessUserOrgUnitDto entity
            Join<AccessUser, AccessUserOrgUnit> orgUnitJoin = root.join("accessUserOrgUnits");

            // Use the orgUnit association from AccessUserOrgUnitDto to get the actual orgUnitId
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

    public static Specification<AccessUser> hasAccessRoleId(String accessRoleId) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Join<AccessUser, AccessAssignment> accessAssignmentJoin = root.join("accessAssignments");
            Path<String> accessRoleIdPath = accessAssignmentJoin.get("accessRole").get("accessRoleId");
            return accessRoleIdPath.in(accessRoleId);
        };
    }
}
