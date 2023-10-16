package no.fintlabs.user;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/accessmanagement/v1/user")
public class AccessUserController {

    private final AccessUserRepository accessUserRepository;

    public AccessUserController(AccessUserRepository accessUserRepository) {
        this.accessUserRepository = accessUserRepository;
    }

    @ApiResponse(description = "Hent alle tilgjengelige roller")
    @GetMapping
    public ResponseEntity<List<AccessUserDto>> getUsers() {
        log.info("Fetching all users");

        return ResponseEntity.ok(accessUserRepository.findAll().stream()
                .map(AccessUserMapper::toDto)
                .collect(java.util.stream.Collectors.toList()));
    }
}
