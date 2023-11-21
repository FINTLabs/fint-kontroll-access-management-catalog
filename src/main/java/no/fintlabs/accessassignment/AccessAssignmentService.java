package no.fintlabs.accessassignment;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.accessassignment.repository.AccessAssignment;
import no.fintlabs.accessassignment.repository.AccessAssignmentId;
import no.fintlabs.accessassignment.repository.AccessAssignmentRepository;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.orgunit.repository.OrgUnit;
import no.fintlabs.orgunit.repository.OrgUnitRepository;
import no.fintlabs.scope.repository.Scope;
import no.fintlabs.scope.repository.ScopeOrgUnit;
import no.fintlabs.scope.repository.ScopeOrgUnitId;
import no.fintlabs.scope.repository.ScopeOrgUnitRepository;
import no.fintlabs.user.repository.AccessUser;
import no.fintlabs.user.repository.AccessUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AccessAssignmentService {
    private final ScopeOrgUnitRepository scopeOrgUnitRepository;
    private final OrgUnitRepository orgUnitRepository;
    private final AccessAssignmentRepository accessAssignmentRepository;
    private final AccessUserRepository accessUserRepository;

    public AccessAssignmentService(ScopeOrgUnitRepository scopeOrgUnitRepository, OrgUnitRepository orgUnitRepository, AccessAssignmentRepository accessAssignmentRepository, AccessUserRepository accessUserRepository) {
        this.scopeOrgUnitRepository = scopeOrgUnitRepository;
        this.orgUnitRepository = orgUnitRepository;
        this.accessAssignmentRepository = accessAssignmentRepository;
        this.accessUserRepository = accessUserRepository;
    }

    public void createScopeOrgUnitRelation(OrgUnit orgUnit, Scope scope) {
        ScopeOrgUnit scopeOrgUnit = ScopeOrgUnit.builder()
                .scopeOrgUnitId(ScopeOrgUnitId.builder()
                                        .scopeId(scope.getId())
                                        .orgUnitId(orgUnit.getOrgUnitId())
                                        .build())
                .scope(scope)
                .build();

        scopeOrgUnitRepository.save(scopeOrgUnit);
    }

    @Transactional
    public AccessAssignment createAssignment(List<String> orgUnitIds, Scope scope, AccessUser accessUser,
                                             AccessRole accessRole) {
        orgUnitIds.forEach(orgUnitId -> orgUnitRepository.findByOrgUnitId(orgUnitId)
                .ifPresentOrElse(orgUnit -> createScopeOrgUnitRelation(orgUnit, scope),
                                 () -> log.error("OrgUnit with id {} does not exist, not saving", orgUnitId)));

        AccessAssignment accessAssignment = createAccessAssignment(scope, accessUser, accessRole);

        log.info("Access assignment for saving {}", accessAssignment);

        AccessAssignment savedAssignment = accessAssignmentRepository.save(accessAssignment);

        log.info("Access assignment saved {}", savedAssignment);

        accessUser.addAccessAssignment(savedAssignment);
        accessUserRepository.save(accessUser);

        return savedAssignment;
    }

    private AccessAssignment createAccessAssignment(Scope scope, AccessUser accessUser, AccessRole accessRole) {
        return AccessAssignment.builder()
                .accessAssignmentId(AccessAssignmentId.builder()
                                            .scopeId(scope.getId())
                                            .userId(accessUser.getUserId())
                                            .accessRoleId(accessRole.getAccessRoleId())
                                            .build())
                .accessUser(accessUser)
                .accessRole(accessRole)
                .scope(scope)
                .build();
    }
}
