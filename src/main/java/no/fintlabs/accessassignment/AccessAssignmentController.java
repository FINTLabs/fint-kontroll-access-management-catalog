package no.fintlabs.accessassignment;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.accessassignment.repository.AccessAssignment;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.accessrole.AccessRoleRepository;
import no.fintlabs.user.repository.AccessUser;
import no.fintlabs.user.repository.AccessUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Tag(name = "Access assignment", description = "Access assignment API - CRUD for access assignments")
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

    @GetMapping("/objecttypes")
    public ResponseEntity<List<String>> getObjectTypes() {
        log.info("Fetching all objecttypes");

        try {
            List<String> objectTypes = accessAssignmentService.getObjectTypes();
            return ResponseEntity.ok(objectTypes);
        } catch (Exception e) {
            log.error("Error getting all objecttypes", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when getting objecttypes");
        }
    }


    @GetMapping("/user/{resourceId}")
    @ResponseBody
    public List<AccessAssignmentDto> getAccessAssignments(@PathVariable(name = "resourceId") String resourceId) {
        log.info("Fetching accessassignments for user {}", resourceId);

        try {
            AccessUser accessUser = accessUserRepository.findByResourceId(resourceId);
            List<AccessAssignment> accessAssignments = accessUser.getAccessAssignments();
            return AccessAssignmentMapper.toDto(accessAssignments);

        } catch (Exception e) {
            log.error("Error getting all accessassignments", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when getting accessassignments");
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public List<AccessAssignmentDto> createAccessAssignments(@RequestBody @Valid UpdateAccessAssignmentDto accessAssignmentDto) {
        log.info("Assigning access to user with data {}", accessAssignmentDto);

        AccessUser accessUser = accessUserRepository.findById(accessAssignmentDto.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));

        AccessRole accessRole = accessRoleRepository.findById(accessAssignmentDto.accessRoleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role does not exist"));

        List<AccessAssignment> currentAssignmentsForRole = accessUser.getAccessAssignments().stream()
                .filter(accessAssignment -> accessAssignment.getAccessRole().equals(accessRole))
                .toList();

        if (!currentAssignmentsForRole.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has assignments for this role, do update.");
        }

        try {

            // TODO: rewrite accessAssignmentDto.scopeId() to being objecttype
            List<AccessAssignment> savedAssignments =
                    accessAssignmentService.createAssignmentsForUser(accessAssignmentDto.orgUnitIds(), "", accessUser, accessRole);

            return AccessAssignmentMapper.toDto(savedAssignments);

        } catch (Exception e) {
            log.error("Error creating accessassignment {}", accessAssignmentDto, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when creating accessassignment");
        }
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<AccessAssignmentDto> updateAccessAssignments(@RequestBody @Valid UpdateAccessAssignmentDto accessAssignmentDto) {
        log.info("Assigning access to user with data {}", accessAssignmentDto);

        AccessUser accessUser = accessUserRepository.findById(accessAssignmentDto.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));

        AccessRole accessRole = accessRoleRepository.findById(accessAssignmentDto.accessRoleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role does not exist"));

        try {

            // TODO: rewrite accessAssignmentDto.scopeId() to being objecttype
            accessAssignmentService.deleteAllAssignmentsByResourceIdAndRole(accessUser, accessRole);
            List<AccessAssignment> savedAssignments =
                    accessAssignmentService.createAssignmentsForUser(accessAssignmentDto.orgUnitIds(), "", accessUser, accessRole);

            return AccessAssignmentMapper.toDto(savedAssignments);

        } catch (Exception e) {
            log.error("Error creating accessassignment {}", accessAssignmentDto, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when creating accessassignment");
        }
    }

    @DeleteMapping("/user/{resourceId}/role/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccessAssignmentByRoleAndObjectType(@PathVariable(name = "resourceId") String resourceId,
                                             @PathVariable(name = "roleId") String roleId,
                                             @RequestParam(name = "objectType", required = false) String objectType) {

        log.info("Deleting accessassignment for user {} with role {}, objecttype {}", resourceId, roleId, objectType);

        try {
            AccessUser accessUser = accessUserRepository.findByResourceId(resourceId);
            AccessRole accessRole = accessRoleRepository.findById(roleId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role does not exist"));

            if(objectType == null || objectType.equalsIgnoreCase("all")) {
                accessAssignmentService.deleteAllAssignmentsByResourceIdAndRole(accessUser, accessRole);
            } else {
                accessAssignmentService.deleteAllAssignmentsByResourceIdAndRoleAndObjectType(accessUser, accessRole, objectType);
            }
        } catch (Exception e) {
            log.error("Error deleting accessassignment for user {} and role {}", resourceId, roleId, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                              String.format("Failed to delete assignments for user %s and role %s", resourceId, roleId));
        }
    }

    @DeleteMapping("/user/{resourceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccessAssignment(@PathVariable(name = "resourceId") String resourceId) {
        log.info("Deleting all assignments for user {}", resourceId);

        try {
            AccessUser accessUser = accessUserRepository.findByResourceId(resourceId);
            accessAssignmentService.deleteAllAssignmentsByResourceId(accessUser);
        } catch (Exception e) {
            log.error("Error deleting accessassignment for user {}", resourceId, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                              String.format("Failed to delete assignments for user %s", resourceId));
        }
    }

    @DeleteMapping("/scope/{scopeId}/orgunit/{orgUnitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrgUnitFromScope(@PathVariable(name = "scopeId") Long scopeId, @PathVariable(name = "orgUnitId") String orgUnitId) {
        log.info("Deleting orgunit {} from scope {}", orgUnitId, scopeId);

        try {
            accessAssignmentService.deleteOrgUnitFromScope(scopeId, orgUnitId);
        } catch (Exception e) {
            log.error("Error deleting orgunit {} from scope {}", orgUnitId, scopeId, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                              String.format("Failed to delete orgunit %s from scope %s", orgUnitId, scopeId));
        }
    }
}
