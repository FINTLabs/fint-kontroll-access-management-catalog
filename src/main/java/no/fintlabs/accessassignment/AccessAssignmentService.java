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
        Set<Long> scopeIds = accessUser.getAccessAssignments().stream()
                .filter(assignment -> assignment.getAccessRole().equals(accessRole))
                .flatMap(assignment -> assignment.getScopes().stream())
                .map(Scope::getId)
                .collect(Collectors.toSet());

        deleteAssignmentsRelations(scopeIds);

        if (!accessUser.getAccessAssignments().isEmpty()) {
            accessAssignmentRepository.deleteAccessAssignmentsByUserIdAndRole(accessUser.getResourceId(), accessRole.getAccessRoleId());
        }
    }

    @Transactional
    public void deleteAllAssignmentsByResourceIdAndRoleAndObjectType(AccessUser accessUser, AccessRole accessRole, String objectType) {
        Set<Long> scopesToDelete = accessUser.getAccessAssignments().stream()
                .filter(assignment -> assignment.getAccessRole().equals(accessRole))
                .flatMap(assignment -> assignment.getScopes().stream())
                .filter(scope -> scope.getObjectType().equals(objectType))
                .map(Scope::getId)
                .collect(Collectors.toSet());


        if (!scopesToDelete.isEmpty()) {
            accessAssignmentRepository.deleteAssignmentScopesByScopeIds(scopesToDelete);
            accessAssignmentRepository.deleteScopeOrgUnitsByScopeIds(scopesToDelete);
            accessAssignmentRepository.deleteScopesByScopeIds(scopesToDelete);
        }

        int scopes = accessAssignmentRepository.countAssignmentScopesByUserRole(accessUser.getResourceId(), accessRole.getAccessRoleId());

        if(scopes == 0) {
            accessAssignmentRepository.deleteAccessAssignmentsByUserIdAndRole(accessUser.getResourceId(), accessRole.getAccessRoleId());
        }
    }

    @Transactional
    public void deleteAllAssignmentsByResourceId(AccessUser accessUser) {
        Set<Long> scopeIds = accessUser.getAccessAssignments().stream()
                .flatMap(assignment -> assignment.getScopes().stream())
                .map(Scope::getId)
                .collect(Collectors.toSet());


        if (!scopeIds.isEmpty()) {
            accessAssignmentRepository.deleteAssignmentScopesByScopeIds(scopeIds);
            accessAssignmentRepository.deleteScopeOrgUnitsByScopeIds(scopeIds);
            accessAssignmentRepository.deleteScopesByScopeIds(scopeIds);
        }

        if (!accessUser.getAccessAssignments().isEmpty()) {
            accessAssignmentRepository.deleteAccessAssignmentsByUserId(accessUser.getResourceId());
        }
    }

    @Transactional
    public void deleteOrgUnitFromScope(Long scopeId, String orgUnitId) {
        if (!scopeRepository.existsById(scopeId)) {
            throw new IllegalArgumentException("Scope with id " + scopeId + " does not exist");
        }

        if (!orgUnitRepository.existsById(orgUnitId)) {
            throw new IllegalArgumentException("OrgUnit with id " + orgUnitId + " does not exist");
        }

        orgUnitRepository.deleteOrgUnitFromScope(scopeId, orgUnitId);
        accessAssignmentRepository.deleteAssignmentScopeWithoutOrgs(scopeId);
        accessAssignmentRepository.deleteScopeWithoutAssignmentScope(scopeId);
    }

    private void deleteAssignmentsRelations(Set<Long> scopeIds) {
        if (!scopeIds.isEmpty()) {
            accessAssignmentRepository.deleteAssignmentScopesByScopeIds(scopeIds);
            accessAssignmentRepository.deleteScopeOrgUnitsByScopeIds(scopeIds);
            accessAssignmentRepository.deleteScopesByScopeIds(scopeIds);
        }
    }

    public List<String> getObjectTypes() {
        return ScopeObjectTypes.getObjectTypesExcludingAll().stream()
                .map(ScopeObjectTypes::getObjectTypeName)
                .collect(Collectors.toList());
    }
}
