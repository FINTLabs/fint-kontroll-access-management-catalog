package no.fintlabs.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.orgunit.repository.OrgUnitInfo;
import no.fintlabs.orgunit.repository.OrgUnitRepository;
import no.fintlabs.user.dto.AccessUserDto;
import no.fintlabs.user.dto.AccessUserOrgUnitsResponseDto;
import no.fintlabs.user.repository.AccessUser;
import no.fintlabs.user.repository.AccessUserRepository;
import no.fintlabs.user.repository.AccessUserSearchCreator;
import org.springdoc.core.annotations.ParameterObject;
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

import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/api/accessmanagement/v1/user")
public class AccessUserController {

    private final AccessUserRepository accessUserRepository;

    private final OrgUnitRepository orgUnitRepository;

    public AccessUserController(AccessUserRepository accessUserRepository, OrgUnitRepository orgUnitRepository) {
        this.accessUserRepository = accessUserRepository;
        this.orgUnitRepository = orgUnitRepository;
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getAllUsers(@RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                           @RequestParam(value = "orgunitid", required = false, defaultValue = "")
                                                           List<String> orgUnitIds,
                                                           @RequestParam(value = "accessroleid", required = false, defaultValue = "")
                                                           String accessRoleId,
                                                           @SortDefault(sort = "resourceId", direction = Sort.Direction.ASC)
                                                           @ParameterObject @PageableDefault(size = 100) Pageable pageable) {
        log.info("Fetching all users with pagination {}", pageable);

        Specification<AccessUser> spec = AccessUserSearchCreator.createAccessUserSearch(name, orgUnitIds, accessRoleId);

        try {
            Page<AccessUser> accessUsers = accessUserRepository.findAll(spec, pageable == null ? Pageable.unpaged() : pageable);
            return ResponseEntity.ok(AccessUserMapper.toAccessUserDtos(accessUsers));
        } catch (Exception e) {
            log.error("Error getting all users", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when getting users");
        }
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<AccessUserDto> getUser(@PathVariable("resourceId") String resourceId) {
        log.info("Fetching user with resourceId {}", resourceId);

        try {
            AccessUser accessUser = accessUserRepository.findById(resourceId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist"));
            return ResponseEntity.ok(AccessUserMapper.toAccessUserDto(accessUser));
        } catch (Exception e) {
            log.error("Error getting user", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when getting users");
        }
    }

    @GetMapping("/{resourceId}/orgunits")
    public ResponseEntity<AccessUserOrgUnitsResponseDto> getOrgUnits(@PathVariable String resourceId,
                                                                     @RequestParam(required = false) String accessRoleId,
                                                                     @RequestParam(required = false) String objectType,
                                                                     @RequestParam(required = false) String orgUnitName,
                                                                     @ParameterObject @PageableDefault(size = 100) Pageable pageable) {

        log.info("Fetching orgunits for user with resourceId {}", resourceId);

        try {
            long startTime = System.currentTimeMillis();

            Page<OrgUnitInfo> orgUnits =
                    orgUnitRepository.findOrgUnitsForUserByFilters(resourceId, accessRoleId, objectType, orgUnitName, pageable);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            log.info("Execution time of findFilteredOrgUnits: " + duration + " milliseconds");

            startTime = System.currentTimeMillis();

            AccessUserOrgUnitsResponseDto mappedOrgUnits = AccessUserMapper.toAccessUserOrgUnitDto(orgUnits);

            endTime = System.currentTimeMillis();
            duration = endTime - startTime;

            log.info("Execution time of mapOrgUnitsResponse2: " + duration + " milliseconds");

            return ResponseEntity.ok(mappedOrgUnits);
        } catch (Exception e) {
            log.error("Error getting orgunits for user", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong when getting orgunits for user");
        }
    }

}
