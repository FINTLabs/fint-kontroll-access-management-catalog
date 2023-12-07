package no.fintlabs.accesspermission;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.accesspermission.repository.AccessPermission;
import no.fintlabs.accesspermission.repository.AccessPermissionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static no.fintlabs.accesspermission.AccessPermissionMapper.toAccessRolePermissionDto;
import static no.fintlabs.accesspermission.AccessPermissionMapper.toAccessRolePermissionDtos;

@Slf4j
@Tag(name = "Access permission", description = "Access permission API - CRUD for access permissions")
@RestController
@RequestMapping("/api/accessmanagement/v1/accesspermission")
public class AccessPermissionController {

    private final AccessPermissionRepository accessPermissionRepository;

    public AccessPermissionController(AccessPermissionRepository accessPermissionRepository) {
        this.accessPermissionRepository = accessPermissionRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public AccessPermissionDto createAccessPermission(@RequestBody AccessPermissionDto accessPermissionDto) {
        log.info("Creating accesspermission {}", accessPermissionDto);

        try {
            AccessPermission accessPermission =
                    accessPermissionRepository.save(AccessPermissionMapper.toAccessPermission(accessPermissionDto));
            return AccessPermissionMapper.toDto(accessPermission);
        } catch (Exception e) {
            log.error("Error creating accesspermission {}", accessPermissionDto, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when creating accesspermission");
        }
    }

    @GetMapping("/accessrole/{accessRoleId}")
    public AccessRolePermissionDto getAccessPermissionsForRole(@PathVariable("accessRoleId") String accessRoleId) {
        log.info("Fetching accesspermissions for accessRoleId {}", accessRoleId);

        try {
            List<AccessPermission> accessPermissions = accessPermissionRepository.findByAccessRoleId(accessRoleId);
            return toAccessRolePermissionDto(accessRoleId, accessPermissions);

        } catch (Exception e) {
            log.error("Error getting all accesspermissions", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when getting accesspermissions");
        }
    }

    @GetMapping("/accessrole")
    public List<AccessRolePermissionDto> getAccessPermissionsForAllRoles() {
        log.info("Fetching accesspermissions for all roles");

        try {
            return toAccessRolePermissionDtos(accessPermissionRepository.findAll());

        } catch (Exception e) {
            log.error("Error getting all accesspermissions", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when getting accesspermissions");
        }
    }

    @PutMapping
    public ResponseEntity<Void> updateAccessPermission(@RequestBody AccessRolePermissionDto dto) {
        log.info("Updating accesspermissions {}", dto);

        List<AccessPermission> existingPermissions = accessPermissionRepository.findByAccessRoleId(dto.getAccessRoleId());

        accessPermissionRepository.deleteAll(existingPermissions);

        List<AccessPermission> newPermissions = AccessPermissionMapper.fromAccessRolePermissionDto(dto);
        accessPermissionRepository.saveAll(newPermissions);

        return ResponseEntity.noContent().build();
    }

}
