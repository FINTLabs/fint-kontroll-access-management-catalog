package no.fintlabs.accessassignment;


import lombok.extern.slf4j.Slf4j;
import no.fintlabs.accesspermission.AccessPermission;
import no.fintlabs.accesspermission.AccessPermissionDto;
import no.fintlabs.accesspermission.AccessPermissionMapper;
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
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public AccessAssignmentDto createAccessPermission(@RequestBody AccessAssignmentDto accessAssignmentDto) {
        log.info("Assigning access to user with data {}", accessAssignmentDto);


        /*try {
            AccessPermission
                    accessPermission = accessPermissionRepository.save(AccessPermissionMapper.toAccessPermission(accessPermissionDto));
            return AccessPermissionMapper.toDto(accessPermission);
        } catch (Exception e) {
            log.error("Error creating accesspermission {}", accessPermissionDto, e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when creating accesspermission");
        }*/
        return null;
    }
}
