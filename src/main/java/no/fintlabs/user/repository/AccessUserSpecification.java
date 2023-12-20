package no.fintlabs.user.repository;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import no.fintlabs.accessassignment.repository.AccessAssignment;
import no.fintlabs.orgunit.repository.OrgUnit;
import no.fintlabs.scope.repository.Scope;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class AccessUserSpecification {

    public static Specification<AccessUser> hasOrganisationUnitIds(List<String> orgUnitIds) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            // Join AccessUser to AccessAssignment
            Join<AccessUser, AccessAssignment> assignmentJoin = root.join("accessAssignments");

            // Join AccessAssignment to Scope
            Join<AccessAssignment, Scope> scopeJoin = assignmentJoin.join("scopes");

            // Join Scope to OrgUnit
            Join<Scope, OrgUnit> orgUnitJoin = scopeJoin.join("orgUnits");

            // Construct the 'in' clause for the search
            return orgUnitJoin.get("orgUnitId").in(orgUnitIds);
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
