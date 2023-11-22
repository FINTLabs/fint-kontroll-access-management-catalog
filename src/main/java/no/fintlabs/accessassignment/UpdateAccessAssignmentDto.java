package no.fintlabs.accessassignment;

import java.util.List;

public record UpdateAccessAssignmentDto(String accessRoleId, Long scopeId, String userId, List<String> orgUnitIds) {
}
