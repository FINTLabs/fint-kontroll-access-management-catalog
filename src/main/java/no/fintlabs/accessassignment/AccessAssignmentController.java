package no.fintlabs.accessassignment;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.accessrole.AccessRoleRepository;
import no.fintlabs.orgunit.OrgUnitRepository;
import no.fintlabs.scope.Scope;
import no.fintlabs.scope.ScopeOrgUnit;
import no.fintlabs.scope.ScopeOrgUnitId;
import no.fintlabs.scope.ScopeOrgUnitRepository;
import no.fintlabs.scope.ScopeRepository;
import no.fintlabs.user.AccessUser;
import no.fintlabs.user.AccessUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/accessmanagement/v1/accessassignment")
public class AccessAssignmentController {
    private final AccessUserRepository accessUserRepository;
    private final AccessAssignmentRepository accessAssignmentRepository;
    private final AccessRoleRepository accessRoleRepository;
    private final ScopeRepository scopeRepository;
    private final OrgUnitRepository orgUnitRepository;
    private final ScopeOrgUnitRepository scopeOrgUnitRepository;

    public AccessAssignmentController(AccessUserRepository accessUserRepository, AccessAssignmentRepository accessAssignmentRepository, AccessRoleRepository accessRoleRepository, ScopeRepository scopeRepository, OrgUnitRepository orgUnitRepository, ScopeOrgUnitRepository scopeOrgUnitRepository) {
        this.accessUserRepository = accessUserRepository;
        this.accessAssignmentRepository = accessAssignmentRepository;
        this.accessRoleRepository = accessRoleRepository;
        this.scopeRepository = scopeRepository;
        this.orgUnitRepository = orgUnitRepository;
        this.scopeOrgUnitRepository = scopeOrgUnitRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public AccessAssignmentDto createAccessPermission(@RequestBody @Valid AccessAssignmentDto accessAssignmentDto) {
        log.info("Assigning access to user with data {}", accessAssignmentDto);

        try {
            AccessUser accessUser = accessUserRepository.findById(accessAssignmentDto.userId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));

            AccessRole accessRole = accessRoleRepository.findById(accessAssignmentDto.accessRoleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role does not exist"));

            Scope scope = scopeRepository.findById(accessAssignmentDto.scopeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Scope does not exist"));

            accessAssignmentDto.orgUnitIds().forEach(orgUnitId -> {
                if (!orgUnitRepository.existsById(orgUnitId)) {
                    log.error("OrgUnit with id {} does not exist, not saving", orgUnitId);
                }

                orgUnitRepository.findById(orgUnitId)
                        .ifPresent(orgUnit -> {
                            ScopeOrgUnit scopeOrgUnit = ScopeOrgUnit.builder()
                                    .scopeOrgUnitId(ScopeOrgUnitId.builder()
                                            .scopeId(scope.getId())
                                            .orgUnitId(orgUnit.getOrgUnitId())
                                            .build())
                                    .scope(scope)
                                    .build();

                            scopeOrgUnitRepository.save(scopeOrgUnit);
                        });
            });

            AccessAssignment accessAssignment = AccessAssignment.builder()
                    .accessAssignmentId(AccessAssignmentId.builder()
                                                .scopeId(scope.getId())
                                                .userId(accessUser.getUserId())
                                                .accessRoleId(accessRole.getAccessRoleId())
                                                .build())
                    .accessUser(accessUser)
                    .scope(scope)
                    .build();

            AccessAssignment savedAssignment = accessAssignmentRepository.save(accessAssignment);

            if(accessUser.getAccessAssignments() == null) {
                accessUser.setAccessAssignments(new ArrayList<>());
            }

            accessUser.getAccessAssignments().add(accessAssignment);
            accessUserRepository.save(accessUser);

            return Optional.of(savedAssignment)
                    .map(AccessAssignmentMapper::toDto)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "AccessAssignment does not exist"));



        } catch (Exception e) {
            log.error("Error creating accessassignment {}", accessAssignmentDto, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when creating accessassignment");
        }
    }
}
