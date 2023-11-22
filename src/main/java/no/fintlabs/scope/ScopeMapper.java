package no.fintlabs.scope;

import no.fintlabs.orgunit.OrgUnitMapper;
import no.fintlabs.scope.repository.Scope;

import java.util.List;

public class ScopeMapper {

    public static ScopeDto toDto(Scope scope) {
        return new ScopeDto(scope.getId(), scope.getObjectType(), OrgUnitMapper.toDto(scope.getOrgUnits()));
    }

    public static List<ScopeDto> toDto(List<Scope> scopes) {
        return scopes.stream()
                .map(ScopeMapper::toDto)
                .toList();
    }
}
