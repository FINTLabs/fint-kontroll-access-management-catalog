package no.fintlabs.user;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.user.dto.AccessUserDto;
import no.fintlabs.user.repository.AccessUser;
import no.fintlabs.user.repository.AccessUserRepository;
import no.fintlabs.user.repository.AccessUserSearchCreator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/accessmanagement/v1/user")
public class AccessUserController {

    private final AccessUserRepository accessUserRepository;

    public AccessUserController(AccessUserRepository accessUserRepository) {
        this.accessUserRepository = accessUserRepository;
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getAllUsers(@RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                           @RequestParam(value = "orgunitid", required = false, defaultValue = "") List<String> orgUnitIds,
                                                           @RequestParam(value = "usertype", required = false, defaultValue = "") String userType,
                                                           @SortDefault(sort = "resourceId", direction = Sort.Direction.ASC)
                                                           @PageableDefault(size = 100) Pageable pageable) {
        log.info("Fetching all users with pagination {}", pageable);

        Specification<AccessUser> spec = AccessUserSearchCreator.createAccessUserSearch(name, orgUnitIds, userType);

        try {
            Page<AccessUser> accessUsers = accessUserRepository.findAll(spec, pageable == null ? Pageable.unpaged() : pageable);
            return ResponseEntity.ok(mapAccessUserResponse(accessUsers));
        } catch (Exception e) {
            log.error("Error getting all users", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when getting users");
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AccessUserDto> getUser(@PathVariable("userId") String userId) {
        log.info("Fetching user with userId {}", userId);

        try {
            AccessUser accessUser = accessUserRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
            return ResponseEntity.ok(AccessUserMapper.toDto(accessUser));
        } catch (Exception e) {
            log.error("Error getting user", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when getting users");
        }
    }

    private Map<String, Object> mapAccessUserResponse(Page<AccessUser> accessUsers) {
        Map<String, Object> response = new HashMap<>();

        response.put("users", accessUsers.getContent()
                .stream()
                .map(AccessUserMapper::toDto)
                .collect(Collectors.toList()));
        response.put("currentPage", accessUsers.getNumber());
        response.put("totalItems", accessUsers.getTotalElements());
        response.put("totalPages", accessUsers.getTotalPages());
        return response;
    }

}
