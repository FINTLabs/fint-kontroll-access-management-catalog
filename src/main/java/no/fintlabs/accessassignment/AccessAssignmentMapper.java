package no.fintlabs.accessassignment;

import no.fintlabs.accessassignment.repository.AccessAssignment;
import no.fintlabs.scope.ScopeMapper;

import java.util.List;

public class AccessAssignmentMapper {

    public static AccessAssignmentDto toDto(AccessAssignment accessAssignment) {
        return new AccessAssignmentDto(accessAssignment.getId(), accessAssignment.getAccessRole().getAccessRoleId(),
                                       ScopeMapper.toDto(accessAssignment.getScopes()),
                                       accessAssignment.getAccessUser().getUserId());
    }

    public static List<AccessAssignmentDto> toDto(List<AccessAssignment> accessAssignments) {
        return accessAssignments.stream()
                .map(AccessAssignmentMapper::toDto)
                .toList();
    }
}
