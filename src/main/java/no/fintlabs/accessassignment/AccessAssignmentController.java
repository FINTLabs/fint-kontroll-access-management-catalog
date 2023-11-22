package no.fintlabs.accessassignment;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.accessassignment.repository.AccessAssignment;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.accessrole.AccessRoleRepository;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/accessmanagement/v1/accessassignment")
public class AccessAssignmentController {

    private final AccessAssignmentService accessAssignmentService;
    private final AccessUserRepository accessUserRepository;
    private final AccessRoleRepository accessRoleRepository;

    public AccessAssignmentController(AccessAssignmentService accessAssignmentService,
                                      AccessUserRepository accessUserRepository,
                                      AccessRoleRepository accessRoleRepository) {
        this.accessAssignmentService = accessAssignmentService;
        this.accessUserRepository = accessUserRepository;
        this.accessRoleRepository = accessRoleRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public List<AccessAssignmentDto> createAccessAssignments(@RequestBody @Valid UpdateAccessAssignmentDto accessAssignmentDto) {
        log.info("Assigning access to user with data {}", accessAssignmentDto);

        try {
            AccessUser accessUser = accessUserRepository.findById(accessAssignmentDto.userId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));

            AccessRole accessRole = accessRoleRepository.findById(accessAssignmentDto.accessRoleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role does not exist"));

            // TODO: rewrite accessAssignmentDto.scopeId() to being objecttype

            List<AccessAssignment> savedAssignments =
                    accessAssignmentService.createAssignmentsForUser(accessAssignmentDto.orgUnitIds(), "", accessUser, accessRole);

            return AccessAssignmentMapper.toDto(savedAssignments);

        } catch (Exception e) {
            log.error("Error creating accessassignment {}", accessAssignmentDto, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when creating accessassignment");
        }
    }
}
