package no.fintlabs.scope.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScopeOrgUnitRepository extends JpaRepository<ScopeOrgUnit, ScopeOrgUnitId> {
}
