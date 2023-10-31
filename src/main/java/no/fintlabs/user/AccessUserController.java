package no.fintlabs.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
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

//
//    get alle roller
//    get med rolle input gir alle permission, feature, operations
//
//    søk med rolle eller feature
//
//    @RequestParam(value = "search",defaultValue = "%") String search,
//    @RequestParam(value = "orgUnits",required = false)List<String> orgUnits,
//    @RequestParam(value = "userType", defaultValue = "ALLTYPES") String userType,

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getAllUsers(@SortDefault(sort = "resourceId", direction = Sort.Direction.ASC) @PageableDefault(size = 100) Pageable pageable){
        log.info("Fetching all users with pagination {}", pageable);

        if (pageable == null) {
            pageable = Pageable.unpaged();
        }

        try {
            Page<AccessUser> accessUsers = accessUserRepository.findAll(pageable);

            Map<String, Object> response = new HashMap<>();

            response.put("users", accessUsers.getContent()
                    .stream()
                    .map(AccessUserMapper::toDto)
                    .collect(Collectors.toList()));
            response.put("currentPage", accessUsers.getNumber());
            response.put("totalItems", accessUsers.getTotalElements());
            response.put("totalPages", accessUsers.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting all users{}", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when getting users");
        }
    }
}
