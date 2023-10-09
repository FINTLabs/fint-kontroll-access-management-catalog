package no.fintlabs.feature;

public class FeatureMapper {

    public static FeatureDto toDto(Feature feature) {
        return new FeatureDto(feature.getId(), feature.getName(), feature.getPath());
    }

    public static Feature toFeature(FeatureDto featureDto) {
        return new Feature(featureDto.id(), featureDto.name(), featureDto.path());
    }

    public static Feature toFeature(FeatureCreationDto featureDto) {
        return Feature.builder().
                name(featureDto.name()).
                path(featureDto.path()).
                build();
    }
}
