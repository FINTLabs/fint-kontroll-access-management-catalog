package no.fintlabs;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.accessrole.AccessRoleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/accessmanagement/v1/accessrole")
public class AccessRoleController {

    private final AccessRoleRepository accessRoleRepository;

    public AccessRoleController(AccessRoleRepository accessRoleRepository) {
        this.accessRoleRepository = accessRoleRepository;
    }

    @ApiResponse(description = "Hent alle tilgjengelige roller")
    @GetMapping
    public ResponseEntity<List<AccessRole>> getAccessRoles() {
        log.info("Fetching all available access roles");
        return ResponseEntity.ok(accessRoleRepository.findAll());
    }
}
