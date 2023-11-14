package no.fintlabs.feature;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.feature.repository.Feature;
import no.fintlabs.feature.repository.FeatureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/accessmanagement/v1/feature")
public class FeatureController {

    private final FeatureRepository featureRepository;

    public FeatureController(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    @GetMapping(value = "/{roleid}")
    @ResponseBody
    public List<AccessRoleFeature> getFeaturesByRole(@PathVariable("roleid") String roleId) {
        log.info("Fetching features for role {}", roleId);

        return new ArrayList<>(featureRepository.findFeaturesByAccessRoleId(roleId));
    }

    @GetMapping()
    @ResponseBody
    public List<FeatureDto> getFeatures() {
        log.info("Fetching all available features");

        return featureRepository.findAll().stream()
                .map(FeatureMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public FeatureDto createFeature(@RequestBody FeatureCreationDto featureDto) {
        log.info("Creating feature {}", featureDto);

        Feature feature = featureRepository.save(FeatureMapper.toFeature(featureDto));
        return FeatureMapper.toDto(feature);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateFeature(@PathVariable("id") Long id, @RequestBody FeatureDto featureDto) {
        log.info("Updating feature {}", featureDto);

        if(!id.equals(featureDto.id())) {
            throw new IllegalArgumentException("Id's do not match");
        }

        Feature feature = FeatureMapper.toFeature(featureDto);
        featureRepository.save(feature);
    }

}
