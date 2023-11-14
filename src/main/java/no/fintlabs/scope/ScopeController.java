package no.fintlabs.scope;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.scope.repository.ScopeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/accessmanagement/v1/scope")
public class ScopeController {

    private final ScopeRepository scopeRepository;

    public ScopeController(ScopeRepository scopeRepository) {
        this.scopeRepository = scopeRepository;
    }

    @ApiResponse(description = "Hent alle tilgjengelige scopes")
    @GetMapping
    @ResponseBody
    public List<ScopeDto> getScopes() {
        log.info("Fetching all available access scopes");

        return scopeRepository.findAll().stream()
                .map(ScopeMapper::toDto)
                .collect(Collectors.toList());
    }
}
