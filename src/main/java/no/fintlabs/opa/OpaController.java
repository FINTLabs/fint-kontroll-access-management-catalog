package no.fintlabs.opa;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final OpaBundleService opaBundleService;

    @Value("${fint.kontroll.opa.api-key:dummykey}")
    private String apiKey;

    @Value("${fint.relations.default-base-url:localhost}")
    private String baseUrl;

    public OpaController(OpaBundleService opaBundleService) {
        this.opaBundleService = opaBundleService;
    }

    @Operation(summary = "Hent OPA bundle", description = "Hent OPA bundle")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping(produces = "application/gzip")
    public ResponseEntity<Resource> getOpaBundle(HttpServletRequest request) throws Exception {
        log.info("THE BASE URL IS: {}, apiKey is: {}", baseUrl, apiKey);
        // TODO: Enable
        /*if (!isValidApiKey(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }*/

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

    private boolean isValidApiKey(HttpServletRequest request) {
        final String apiKeyHeaderName = "X-Api-Key";

        String apiKeyFromOpa = request.getHeader(apiKeyHeaderName) != null ? request.getHeader(apiKeyHeaderName) : "";
        return apiKey.equals(apiKeyFromOpa);
    }


}
