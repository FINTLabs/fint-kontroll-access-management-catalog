package no.fintlabs.scope;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import no.fintlabs.orgunit.OrgUnitDto;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonSerialize
public record ScopeDto(Long id, String objectType, List<OrgUnitDto> orgUnits) {
}
