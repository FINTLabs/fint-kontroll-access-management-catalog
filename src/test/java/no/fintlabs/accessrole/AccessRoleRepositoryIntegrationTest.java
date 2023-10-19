package no.fintlabs.accessrole;

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
public class AccessRoleRepositoryIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test")
            .withUsername("user")
            .withPassword("password");

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
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @BeforeEach
    void setup() {
        // Initialize test data
        AccessRole role1 = AccessRole.builder()
                .accessRoleId("AR1")
                .name("Role1")
                .build();

        AccessRole role2 = AccessRole.builder()
                .accessRoleId("AR2")
                .name("Role2")
                .build();

        accessRoleRepository.save(role1);
        accessRoleRepository.save(role2);
    }

    @Test
    void shouldFindAllRoles() {
        List<AccessRole> roles = accessRoleRepository.findAll();

        assertEquals(2, roles.size());

        AccessRole role1 = roles.get(0);
        assertThat(role1.getAccessRoleId()).isEqualTo("AR1");
        assertThat(role1.getName()).isEqualTo("Role1");

        AccessRole role2 = roles.get(1);
        assertThat(role2.getAccessRoleId()).isEqualTo("AR2");
        assertThat(role2.getName()).isEqualTo("Role2");

    }
}

