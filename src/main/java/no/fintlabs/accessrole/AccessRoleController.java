package no.fintlabs.accessrole;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Tag(name = "Role", description = "Role API - Get all available roles")
@RestController
@RequestMapping("/api/accessmanagement/v1/accessrole")
public class AccessRoleController {

    private final AccessRoleRepository accessRoleRepository;

    public AccessRoleController(AccessRoleRepository accessRoleRepository) {
        this.accessRoleRepository = accessRoleRepository;
    }

    @Operation(summary = "Get all available roles")
    @ApiResponse(responseCode = "200", description = "All available roles")
    @GetMapping
    public ResponseEntity<List<AccessRole>> getAccessRoles() {
        log.info("Fetching all available access roles");
        return ResponseEntity.ok(accessRoleRepository.findAll());
    }
}
