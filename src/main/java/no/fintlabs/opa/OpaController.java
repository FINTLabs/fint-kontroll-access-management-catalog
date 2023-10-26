package no.fintlabs.opa;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/api/accessmanagement/v1/opabundle")
public class OpaController {

    @ApiResponse(description = "Hent OPA bundle")
    @GetMapping
    public ResponseEntity<Resource> getOpaBundle() {
        try {
            log.info("Getting OPA bundle");
            Path file = Paths.get(System.getProperty("java.io.tmpdir") + "/opabundle/", "opabundle.tar.gz");
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists()) {
                log.info("Found OPA bundle, returning");
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("application/gzip"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                log.info("OPA bundle not found");
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error reading the bundle file!", e);
        }
    }

}
