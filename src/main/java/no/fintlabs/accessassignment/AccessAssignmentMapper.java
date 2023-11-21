package no.fintlabs.accessassignment;

import no.fintlabs.accessassignment.repository.AccessAssignment;

import java.util.List;

public class AccessAssignmentMapper {

    public static AccessAssignmentDto toDto(AccessAssignment accessAssignment) {
        return new AccessAssignmentDto(accessAssignment.getAccessAssignmentId().getAccessRoleId(), accessAssignment.getAccessAssignmentId()
                .getScopeId(), accessAssignment.getAccessAssignmentId().getUserId(), getScopeOrgUnits(accessAssignment));
    }

    private static List<String> getScopeOrgUnits(AccessAssignment accessAssignment) {
        return accessAssignment.getScope().getScopeOrgUnits().stream()
                .map(scopeOrgUnit -> scopeOrgUnit.getScopeOrgUnitId().getOrgUnitId())
                .toList();
    }
}
