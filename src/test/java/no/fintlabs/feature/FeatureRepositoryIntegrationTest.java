package no.fintlabs.feature;

import no.fintlabs.accesspermission.AccessPermission;
import no.fintlabs.accesspermission.AccessPermissionRepository;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.accessrole.AccessRoleRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Testcontainers
public class FeatureRepositoryIntegrationTest {
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test")
            .withUsername("user")
            .withPassword("password");

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private AccessPermissionRepository accessPermissionRepository;

    @Autowired
    private AccessRoleRepository accessRoleRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.flyway.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.flyway.user", postgreSQLContainer::getUsername);
        registry.add("spring.flyway.password", postgreSQLContainer::getPassword);
    }

    @Test
    void shouldFindAllFeatures() {
        Feature feature1 = createFeature("Feature1", "/api/feature1");
        Feature feature2 = createFeature("Feature2", "/api/feature2");

        List<Feature> features = featureRepository.findAll();

        features.forEach(feature -> {
            if (feature.getId().equals(feature1.getId())) {
                assertThat(feature.getName()).isEqualTo("Feature1");
                assertThat(feature.getPath()).isEqualTo("/api/feature1");
            }

            if (feature.getId().equals(feature2.getId())) {
                assertThat(feature.getName()).isEqualTo("Feature2");
                assertThat(feature.getPath()).isEqualTo("/api/feature2");
            }
        });
    }

    @Test
    void shouldFindFeaturesForGivenRole() {
        Feature feature1 = createFeature("Feature1", "/api/feature1");
        AccessRole role = createRole("ata", "applikasjonstilgangsadministrator");
        createAccessPermission(feature1.getId(), role.getAccessRoleId(), "GET");

        List<AccessRoleFeature> features = featureRepository.findFeaturesByAccessRoleId("ata");

        assertEquals(1, features.size());

        AccessRoleFeature accessRoleFeature = features.get(0);
        assertThat(accessRoleFeature.getName()).isEqualTo("Feature1");
        assertThat(accessRoleFeature.getPath()).isEqualTo("/api/feature1");
        assertThat(accessRoleFeature.getOperation()).isEqualTo("GET");
    }

    private void createAccessPermission(Long featureId, String accessRoleId, String operation) {
        AccessPermission accessPermission = AccessPermission.builder()
                .featureId(featureId)
                .accessRoleId(accessRoleId)
                .operation(operation)
                .build();

        accessPermissionRepository.save(accessPermission);
    }

    @NotNull
    private AccessRole createRole(String accessRoleId, String name) {
        AccessRole role = AccessRole.builder()
                .accessRoleId(accessRoleId)
                .name(name)
                .build();

        accessRoleRepository.save(role);
        return role;
    }

    @NotNull
    private Feature createFeature(String name, String path) {
        Feature feature1 = Feature.builder()
                .name(name)
                .path(path)
                .build();

        featureRepository.save(feature1);
        return feature1;
    }
}

