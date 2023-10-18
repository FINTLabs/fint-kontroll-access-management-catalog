package no.fintlabs.accessassignment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record AccessAssignmentDto (String accessRoleId, Long scopeId, String userId, List<String> orgUnitIds) {
}
