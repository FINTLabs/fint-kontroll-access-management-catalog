package no.fintlabs.accessassignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface AccessAssignmentRepository extends JpaRepository<AccessAssignment, Long> {

    List<AccessAssignment> findByAccessUserResourceId(String resourceId);

    @Query(value = "SELECT COUNT(*) FROM assignment_scope WHERE scope_id = :scopeId", nativeQuery = true)
    int countAssignmentScopesByScopeId(Long scopeId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM assignment_scope WHERE scope_id IN :scopeIds", nativeQuery = true)
    void deleteAssignmentScopesByScopeIds(Set<Long> scopeIds);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM accessassignment WHERE user_id = :userId", nativeQuery = true)
    void deleteAccessAssignmentsByUserId(String userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM scope_orgunit WHERE scope_id IN :scopeIds", nativeQuery = true)
    void deleteScopeOrgUnitsByScopeIds(Set<Long> scopeIds);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM scope WHERE id IN :scopeIds AND NOT EXISTS (" +
                   "SELECT 1 FROM assignment_scope WHERE scope_id = id" +
                   ")", nativeQuery = true)
    void deleteScopesByScopeIds(Set<Long> scopeIds);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM assignment_scope " +
                   "WHERE scope_id = :scopeId AND NOT EXISTS (" +
                   "SELECT 1 FROM scope_orgunit WHERE scope_id = :scopeId" +
                   ")", nativeQuery = true)
    void deleteAssignmentScopeWithoutOrgs(Long scopeId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM scope " +
                   "WHERE id = :scopeId AND NOT EXISTS (" +
                   "SELECT 1 FROM assignment_scope WHERE scope_id = :scopeId" +
                   ")", nativeQuery = true)
    void deleteScopeWithoutAssignmentScope(Long scopeId);
}
