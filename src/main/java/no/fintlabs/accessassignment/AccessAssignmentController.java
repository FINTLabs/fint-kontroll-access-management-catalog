package no.fintlabs.accessassignment;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.accessassignment.repository.AccessAssignment;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.accessrole.AccessRoleRepository;
import no.fintlabs.scope.repository.Scope;
import no.fintlabs.scope.repository.ScopeRepository;
import no.fintlabs.user.repository.AccessUser;
import no.fintlabs.user.repository.AccessUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/api/accessmanagement/v1/accessassignment")
public class AccessAssignmentController {

    private final AccessAssignmentService accessAssignmentService;
    private final AccessUserRepository accessUserRepository;
    private final AccessRoleRepository accessRoleRepository;
    private final ScopeRepository scopeRepository;

    public AccessAssignmentController(AccessAssignmentService accessAssignmentService,
                                      AccessUserRepository accessUserRepository,
                                      AccessRoleRepository accessRoleRepository,
                                      ScopeRepository scopeRepository) {
        this.accessAssignmentService = accessAssignmentService;
        this.accessUserRepository = accessUserRepository;
        this.accessRoleRepository = accessRoleRepository;
        this.scopeRepository = scopeRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public AccessAssignmentDto createAccessAssignment(@RequestBody @Valid AccessAssignmentDto accessAssignmentDto) {
        log.info("Assigning access to user with data {}", accessAssignmentDto);

        try {

            AccessUser accessUser = accessUserRepository.findById(accessAssignmentDto.userId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));

            AccessRole accessRole = accessRoleRepository.findById(accessAssignmentDto.accessRoleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role does not exist"));

            Scope scope = scopeRepository.findById(accessAssignmentDto.scopeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Scope does not exist"));

            AccessAssignment savedAssignment =
                    accessAssignmentService.createAssignment(accessAssignmentDto.orgUnitIds(), scope, accessUser, accessRole);

            return AccessAssignmentMapper.toDto(savedAssignment);

        } catch (Exception e) {
            log.error("Error creating accessassignment {}", accessAssignmentDto, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when creating accessassignment");
        }
    }

}
