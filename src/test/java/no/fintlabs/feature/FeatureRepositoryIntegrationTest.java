package no.fintlabs.feature;

import no.fintlabs.accessrole.AccessRole;
import org.junit.jupiter.api.BeforeEach;
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

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.flyway.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.flyway.user", postgreSQLContainer::getUsername);
        registry.add("spring.flyway.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setup() {
        Feature feature1 = Feature.builder()
                .name("Feature1")
                .path("/api/feature1")
                .build();

        Feature feature2 = Feature.builder()
                .name("Feature2")
                .path("/api/feature2")
                .build();

        featureRepository.save(feature1);
        featureRepository.save(feature2);
    }

    @Test
    void shouldFindAllFeatures() {
        List<Feature> features = featureRepository.findAll();

        assertEquals(2, features.size());

        Feature feature1 = features.get(0);
        assertThat(feature1.getName()).isEqualTo("Feature1");
        assertThat(feature1.getPath()).isEqualTo("/api/feature1");

        Feature feature2 = features.get(1);
        assertThat(feature2.getName()).isEqualTo("Feature2");
        assertThat(feature2.getPath()).isEqualTo("/api/feature2");
    }
}

