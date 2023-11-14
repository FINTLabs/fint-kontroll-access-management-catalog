package no.fintlabs.scope;

import no.fintlabs.scope.repository.Scope;

public class ScopeMapper {

    public static ScopeDto toDto(Scope scope) {
        return new ScopeDto(scope.getId(), scope.getObjectType());
    }
}
