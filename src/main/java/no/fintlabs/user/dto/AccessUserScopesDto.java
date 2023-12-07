package no.fintlabs.user.dto;

import no.fintlabs.orgunit.OrgUnitDto;

import java.util.List;

public record AccessUserScopesDto(Long scopeId, String objectType, List<OrgUnitDto> orgUnits) {
}
