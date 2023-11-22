package no.fintlabs.accessassignment;

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

@Slf4j
@Service
public class AccessAssignmentService {

    private final ScopeRepository scopeRepository;
    private final OrgUnitRepository orgUnitRepository;
    private final AccessAssignmentRepository accessAssignmentRepository;
    private final AccessUserRepository accessUserRepository;

    public AccessAssignmentService(ScopeRepository scopeRepository, OrgUnitRepository orgUnitRepository,
                                   AccessAssignmentRepository accessAssignmentRepository, AccessUserRepository accessUserRepository) {
        this.scopeRepository = scopeRepository;
        this.orgUnitRepository = orgUnitRepository;
        this.accessAssignmentRepository = accessAssignmentRepository;
        this.accessUserRepository = accessUserRepository;
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

        AccessAssignment createdAssignment = accessAssignmentRepository.save(accessAssignment);
        accessUser.addAccessAssignment(createdAssignment);

        accessUserRepository.save(accessUser);
        return accessUser.getAccessAssignments();
    }
}
