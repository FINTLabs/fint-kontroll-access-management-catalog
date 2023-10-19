package no.fintlabs.accessassignment;

import no.fintlabs.orgunit.OrgUnit;
import no.fintlabs.orgunit.OrgUnitRepository;
import no.fintlabs.scope.Scope;
import no.fintlabs.scope.ScopeOrgUnit;
import no.fintlabs.scope.ScopeOrgUnitId;
import no.fintlabs.scope.ScopeOrgUnitRepository;
import no.fintlabs.scope.ScopeRepository;
import no.fintlabs.user.AccessUser;
import no.fintlabs.user.AccessUserRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
public class AccessAssignmentRepositoryIntegrationTest {

    @Autowired
    private AccessAssignmentRepository accessAssignmentRepository;

    @Autowired
    private OrgUnitRepository orgUnitRepository;

    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private ScopeOrgUnitRepository scopeOrgUnitRepository;

    @Autowired
    private AccessUserRepository accessUserRepository;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test")
            .withUsername("user")
            .withPassword("password");

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

    @Test
    public void shouldCreateNewAssignmentForUser() {
        OrgUnit orgUnit1 = createOrgUnit("198");
        OrgUnit orgUnit2 = createOrgUnit("153");

        Scope scope = createOrgUnitScope("orgunit");

        ScopeOrgUnit scopeOrgUnit1 = createScopeOrgUnitRelation(scope, orgUnit1);
        ScopeOrgUnit scopeOrgUnit2 = createScopeOrgUnitRelation(scope, orgUnit2);

        List<ScopeOrgUnit> scopeOrgUnits = new ArrayList<>();
        scopeOrgUnits.add(scopeOrgUnit1);
        scopeOrgUnits.add(scopeOrgUnit2);
        scope.setScopeOrgUnits(scopeOrgUnits);

        Scope updatedScope = scopeRepository.save(scope);

        AccessUser accessUser = createAccessUser();

        AccessAssignment accessAssignment = createAccessAssignment(updatedScope, accessUser);

        List<AccessAssignment> accessAssignments = new ArrayList<>();
        accessAssignments.add(accessAssignment);

        accessUser.setAccessAssignments(accessAssignments);
        AccessUser updatedAccessUser = accessUserRepository.save(accessUser);

        accessUserRepository.findById(accessUser.getResourceId()).ifPresent(foundAccessUser -> {
            assertThat(foundAccessUser).isEqualTo(updatedAccessUser);
            assertThat(foundAccessUser.getAccessAssignments()).hasSize(1);
            assertThat(foundAccessUser.getAccessAssignments().get(0).getScope().getObjectType()).isEqualTo("orgunit");
            assertThat(foundAccessUser.getAccessAssignments().get(0).getScope().getScopeOrgUnits()).hasSize(2);
            assertThat(foundAccessUser.getAccessAssignments().get(0).getScope().getScopeOrgUnits().get(0).getOrgUnit()
                               .getOrgUnitId()).isEqualTo("198");
            assertThat(foundAccessUser.getAccessAssignments().get(0).getScope().getScopeOrgUnits().get(1).getOrgUnit()
                               .getOrgUnitId()).isEqualTo("153");
        });

    }

    @NotNull
    private AccessAssignment createAccessAssignment(Scope savedOrgUnitScope, AccessUser savedAccessUser) {
        AccessAssignment accessAssignment = AccessAssignment.builder()
                .accessAssignmentId(AccessAssignmentId.builder()
                                            .scopeId(savedOrgUnitScope.getId())
                                            .userId(savedAccessUser.getUserId())
                                            .accessRoleId("ata")
                                            .build())
                .accessUser(savedAccessUser)
                .scope(savedOrgUnitScope)
                .build();

        return accessAssignmentRepository.save(accessAssignment);
    }

    @NotNull
    private AccessUser createAccessUser() {
        AccessUser accessUser = AccessUser.builder()
                .resourceId("123")
                .userId("123")
                .userName("morten.solberg@vigoiks.no")
                .build();

        return accessUserRepository.save(accessUser);
    }

    @NotNull
    private ScopeOrgUnit createScopeOrgUnitRelation(Scope savedOrgUnitScope, OrgUnit orgUnit) {
        ScopeOrgUnit scopeOrgUnit = ScopeOrgUnit.builder()
                .scopeOrgUnitId(ScopeOrgUnitId.builder()
                                        .scopeId(savedOrgUnitScope.getId())
                                        .orgUnitId(orgUnit.getId())
                                        .build())
                .scope(savedOrgUnitScope)
                .orgUnit(orgUnit)
                .build();

        return scopeOrgUnitRepository.save(scopeOrgUnit);
    }

    @NotNull
    private Scope createOrgUnitScope(String scope) {
        Scope orgUnitScope = Scope.builder()
                .objectType(scope)
                .build();

        return scopeRepository.save(orgUnitScope);
    }

    private OrgUnit createOrgUnit(String orgUnitId) {
        OrgUnit orgUnit = OrgUnit.builder()
                .orgUnitId(orgUnitId)
                .build();
        return orgUnitRepository.save(orgUnit);
    }
}
