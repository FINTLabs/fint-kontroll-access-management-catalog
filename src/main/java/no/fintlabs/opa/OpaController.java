package no.fintlabs.opa;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "OPA", description = "OPA API for bundle")
@RestController
@RequestMapping("/api/accessmanagement/v1/opabundle")
public class OpaController {

    @Autowired
    private OpaBundleService opaBundleService;

    @Operation(summary = "Hent OPA bundle", description = "Hent OPA bundle")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping(produces = "application/gzip")
    public ResponseEntity<Resource> getOpaBundle() throws Exception {
        try {
            Resource resource = opaBundleService.getOpaBundleFile();

            log.info("Getting OPA bundle from {}", resource.getURI());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("application/gzip"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                log.error("OPA bundle not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error reading the bundle file!", e);
            throw e;
        }
    }



}
