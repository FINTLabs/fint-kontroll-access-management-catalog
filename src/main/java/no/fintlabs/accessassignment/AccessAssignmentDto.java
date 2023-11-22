package no.fintlabs.accessassignment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import no.fintlabs.scope.ScopeDto;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record AccessAssignmentDto (Long id, String accessRoleId, List<ScopeDto> scopes, String userId) {
}
