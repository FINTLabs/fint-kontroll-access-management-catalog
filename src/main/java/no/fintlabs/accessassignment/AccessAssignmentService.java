package no.fintlabs.accessassignment;

import no.fintlabs.orgunit.OrgUnit;
import no.fintlabs.scope.Scope;
import no.fintlabs.scope.ScopeOrgUnit;
import no.fintlabs.scope.ScopeOrgUnitId;
import no.fintlabs.scope.ScopeOrgUnitRepository;
import org.springframework.stereotype.Service;

@Service
public class AccessAssignmentService {
    private final ScopeOrgUnitRepository scopeOrgUnitRepository;

    public AccessAssignmentService(ScopeOrgUnitRepository scopeOrgUnitRepository) {
        this.scopeOrgUnitRepository = scopeOrgUnitRepository;
    }

    public void createScopeOrgUnitRelation(OrgUnit orgUnit, Scope scope) {
        ScopeOrgUnit scopeOrgUnit = ScopeOrgUnit.builder()
                .scopeOrgUnitId(ScopeOrgUnitId.builder()
                                        .scopeId(scope.getId())
                                        .orgUnitId(orgUnit.getId())
                                        .build())
                .scope(scope)
                .build();

        scopeOrgUnitRepository.save(scopeOrgUnit);
    }
}
