package no.fintlabs.accessassignment;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.accessassignment.repository.AccessAssignment;
import no.fintlabs.accessassignment.repository.AccessAssignmentRepository;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.orgunit.repository.OrgUnit;
import no.fintlabs.orgunit.repository.OrgUnitRepository;
import no.fintlabs.scope.ScopeObjectTypes;
import no.fintlabs.scope.repository.Scope;
import no.fintlabs.scope.repository.ScopeRepository;
import no.fintlabs.user.repository.AccessUser;
import no.fintlabs.user.repository.AccessUserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccessAssignmentService {

    private final ScopeRepository scopeRepository;
    private final OrgUnitRepository orgUnitRepository;
    private final AccessAssignmentRepository accessAssignmentRepository;
    private final AccessUserRepository accessUserRepository;

    private EntityManager entityManager;

    public AccessAssignmentService(ScopeRepository scopeRepository, OrgUnitRepository orgUnitRepository,
                                   AccessAssignmentRepository accessAssignmentRepository, AccessUserRepository accessUserRepository,
                                   EntityManager entityManager) {
        this.scopeRepository = scopeRepository;
        this.orgUnitRepository = orgUnitRepository;
        this.accessAssignmentRepository = accessAssignmentRepository;
        this.accessUserRepository = accessUserRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public List<AccessAssignment> createAssignmentsForUser(List<String> orgUnitIds, String objectType, AccessUser accessUser,
                                                           AccessRole accessRole) {

        List<OrgUnit> orgUnits = new ArrayList<>();

        orgUnitIds.forEach(orgUnitId -> orgUnitRepository.findByOrgUnitId(orgUnitId)
                .ifPresentOrElse(orgUnits::add, () -> log.error("OrgUnit with id {} does not exist, not saving", orgUnitId)));

        // Create scope for all enums temporary until frontend support objecttypes
        List<Scope> createdScopes = new ArrayList<>();

        ScopeObjectTypes.getObjectTypesExcludingAll().forEach(objectTypeEnum -> {
            Scope scope = Scope.builder()
                    .orgUnits(orgUnits)
                    .objectType(objectTypeEnum.getObjectTypeName())
                    .build();

            Scope createdScope = scopeRepository.save(scope);
            createdScopes.add(createdScope);
        });

        AccessAssignment accessAssignment = AccessAssignment.builder()
                .accessRole(accessRole)
                .accessUser(accessUser)
                .scopes(createdScopes)
                .build();

        accessAssignmentRepository.save(accessAssignment);

        return accessAssignmentRepository.findByAccessUserResourceId(accessUser.getResourceId());
    }

    @Transactional
    public void deleteAllAssignmentsByResourceIdAndRole(AccessUser accessUser, AccessRole accessRole) {
        // Collect all Scope IDs associated with these assignments and role
        Set<Long> scopeIds = accessUser.getAccessAssignments().stream()
                .filter(assignment -> assignment.getAccessRole().equals(accessRole))
                .flatMap(assignment -> assignment.getScopes().stream())
                .map(Scope::getId)
                .collect(Collectors.toSet());

        if(scopeIds.isEmpty()) {
            log.info("No assignments to delete for user {}", accessUser.getResourceId());
            return;
        }

        log.info("Delete from assignment_scope where assignment_id in {}", scopeIds);
        // Delete entries from assignment_scope join table
        entityManager.createNativeQuery("DELETE FROM assignment_scope WHERE scope_id IN :ids")
                .setParameter("ids", scopeIds)
                .executeUpdate();

        log.info("Delete from accessassignment where user_id = {} and access_role_id = {}", accessUser.getResourceId(), accessRole.getAccessRoleId());
        // Delete AccessAssignments with given role for the user
        entityManager.createNativeQuery("DELETE FROM accessassignment WHERE user_id = :userId AND access_role_id = :roleId")
                .setParameter("userId", accessUser.getResourceId())
                .setParameter("roleId", accessRole.getAccessRoleId())
                .executeUpdate();

        // Delete entries from scope_orgunit join table for scopes that are about to be deleted using native SQL
        entityManager.createNativeQuery("DELETE FROM scope_orgunit WHERE scope_id IN :ids")
                .setParameter("ids", scopeIds)
                .executeUpdate();

        // Now, delete scopes if they are no longer referenced in assignment_scope using native SQL
        entityManager.createNativeQuery(
                        "DELETE FROM scope " +
                        "WHERE id IN :ids AND NOT EXISTS (" +
                        "SELECT 1 FROM assignment_scope WHERE scope_id = id" +
                        ")"
                )
                .setParameter("ids", scopeIds)
                .executeUpdate();
    }

    @Transactional
    public void deleteAllAssignmentsByResourceId(AccessUser accessUser) {
        // Collect all Scope IDs associated with these assignments
        Set<Long> scopeIds = accessUser.getAccessAssignments().stream()
                .flatMap(assignment -> assignment.getScopes().stream())
                .map(Scope::getId)
                .collect(Collectors.toSet());

        if(scopeIds.isEmpty()) {
            log.info("No assignments to delete for user {}", accessUser.getResourceId());
            return;
        }

        // Delete entries from assignment_scope join table
        entityManager.createNativeQuery("DELETE FROM assignment_scope WHERE scope_id IN :ids")
                .setParameter("ids", scopeIds)
                .executeUpdate();

        // Delete AccessAssignments for the user
        entityManager.createNativeQuery("DELETE FROM accessassignment WHERE user_id = :userId")
                .setParameter("userId", accessUser.getResourceId())
                .executeUpdate();

        // Delete entries from scope_orgunit join table for scopes that are about to be deleted using native SQL
        entityManager.createNativeQuery("DELETE FROM scope_orgunit WHERE scope_id IN :ids")
                .setParameter("ids", scopeIds)
                .executeUpdate();

        // Now, delete scopes if they are no longer referenced in assignment_scope using native SQL
        entityManager.createNativeQuery(
                        "DELETE FROM scope " +
                        "WHERE id IN :ids AND NOT EXISTS (" +
                        "SELECT 1 FROM assignment_scope WHERE scope_id = id" +
                        ")"
                )
                .setParameter("ids", scopeIds)
                .executeUpdate();
    }

    @Transactional
    public void deleteOrgUnitFromScope(Long scopeId, String orgUnitId) {
        if (!scopeRepository.existsById(scopeId)) {
            throw new IllegalArgumentException("Scope with id " + scopeId + " does not exist");
        }

        if (!orgUnitRepository.existsById(orgUnitId)) {
            throw new IllegalArgumentException("OrgUnit with id " + orgUnitId + " does not exist");
        }

        entityManager.createNativeQuery("DELETE FROM scope_orgunit WHERE scope_id = :scopeId AND org_unit_id = :orgUnitId")
                .setParameter("scopeId", scopeId)
                .setParameter("orgUnitId", orgUnitId)
                .executeUpdate();
    }

    @Transactional
    public void deleteOrgUnitsForUserByObjectType(String userId, String objectType) {
        entityManager.createNativeQuery(
                        "DELETE FROM scope_orgunit so " +
                        "USING scope s " +
                        "WHERE so.scope_id = s.id " +
                        "AND s.id IN (SELECT scope_id FROM accessassignment aa WHERE aa.user_id = :userId) " +
                        "AND s.objecttype = :objectType"
                )
                .setParameter("userId", userId)
                .setParameter("objectType", objectType)
                .executeUpdate();
    }
}
