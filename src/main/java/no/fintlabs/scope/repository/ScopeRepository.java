package no.fintlabs.scope.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ScopeRepository extends JpaRepository<Scope, Long> {
    @Query(value = "SELECT COUNT(*) FROM scope_orgunit WHERE scope_id = :scopeId", nativeQuery = true)
    int countScopeOrgUnitsByScopeId(Long scopeId);
}
