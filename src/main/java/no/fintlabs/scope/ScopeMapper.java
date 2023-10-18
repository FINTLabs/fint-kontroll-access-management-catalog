package no.fintlabs.scope;

public class ScopeMapper {

    public static ScopeDto toDto(Scope scope) {
        return new ScopeDto(scope.getId(), scope.getObjectType());
    }
}
