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
public class UserController {
    @ApiResponse(description = "Hent alle tilgjengelige roller")
    @GetMapping
    public ResponseEntity<List<AccessUser>> getUsers() {
        log.info("Fetching all users");

        AccessUser user1 = AccessUser.builder()
                .resourceId("237")
                .userId("237")
                .userName("morten.solberg@vigoiks.no")
                .firstName("Morten")
                .lastName("Solberg")
                .build();

        return ResponseEntity.ok(List.of(user1));
    }
}
