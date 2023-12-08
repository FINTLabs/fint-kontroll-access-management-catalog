package no.fintlabs.accessassignment;

import no.fintlabs.accessassignment.repository.AccessAssignment;
import no.fintlabs.accessassignment.repository.AccessAssignmentRepository;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.accessrole.AccessRoleRepository;
import no.fintlabs.orgunit.repository.OrgUnit;
import no.fintlabs.orgunit.repository.OrgUnitRepository;
import no.fintlabs.scope.repository.Scope;
import no.fintlabs.scope.repository.ScopeRepository;
import no.fintlabs.user.repository.AccessUser;
import no.fintlabs.user.repository.AccessUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({AccessAssignmentService.class})
@Testcontainers
public class AccessAssignmentRepositoryIntegrationTest {

    @Autowired
    private AccessAssignmentRepository accessAssignmentRepository;

    @Autowired
    private OrgUnitRepository orgUnitRepository;

    @Autowired
    private ScopeRepository scopeRepository;

    @Autowired
    private AccessUserRepository accessUserRepository;

    @Autowired
    private AccessRoleRepository accessRoleRepository;

    @Autowired
    private AccessAssignmentService accessAssignmentService;

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
        OrgUnit orgUnit1 = createOrgUnit("198", "org 1");
        OrgUnit orgUnit2 = createOrgUnit("153", "org 2");

        Scope scope = createScope("orgunit", List.of(orgUnit1, orgUnit2));

        AccessUser accessUser = createAccessUser();
        AccessRole accessRole = createAccessRole("ata", "Access to all");

        AccessAssignment accessAssignment = createAccessAssignment(List.of(scope), accessUser, accessRole);

        List<AccessAssignment> accessAssignments = new ArrayList<>();
        accessAssignments.add(accessAssignment);

        accessUser.setAccessAssignments(accessAssignments);
        AccessUser updatedAccessUser = accessUserRepository.save(accessUser);

        accessUserRepository.findById(accessUser.getResourceId()).ifPresent(foundAccessUser -> {
            assertThat(foundAccessUser).isEqualTo(updatedAccessUser);
            assertThat(foundAccessUser.getAccessAssignments()).hasSize(1);

            AccessAssignment foundAccessAssignment = foundAccessUser.getAccessAssignments().get(0);
            assertThat(foundAccessAssignment.getAccessRole().getAccessRoleId()).isEqualTo("ata");
            assertThat(foundAccessAssignment.getAccessUser().getUserId()).isEqualTo("123");

            List<Scope> foundScopes = foundAccessAssignment.getScopes();

            assertThat(foundScopes).hasSize(1);
            assertThat(foundScopes.get(0).getObjectType()).isEqualTo("orgunit");

            List<OrgUnit> orgUnits = foundScopes.get(0).getOrgUnits();

            assertThat(orgUnits).hasSize(2);
            assertThat(orgUnits.get(0).getOrgUnitId()).isEqualTo("198");
            assertThat(orgUnits.get(1).getOrgUnitId()).isEqualTo("153");
        });
    }

    @Test
    public void shouldDeleteAllAssignmentsByResourceId() {
        OrgUnit orgUnit1 = createOrgUnit("198", "org 1");
        OrgUnit orgUnit2 = createOrgUnit("153", "org 2");

        Scope scope = createScope("orgunit", List.of(orgUnit1, orgUnit2));

        AccessUser accessUser = createAccessUser();
        AccessRole accessRole = createAccessRole("ata", "Access to all");

        AccessAssignment accessAssignment = createAccessAssignment(List.of(scope), accessUser, accessRole);

        List<AccessAssignment> accessAssignments = new ArrayList<>();
        accessAssignments.add(accessAssignment);

        accessUser.setAccessAssignments(accessAssignments);
        AccessUser updatedAccessUser = accessUserRepository.save(accessUser);

        verifyDataInserted(accessUser);

        accessAssignmentService.deleteAllAssignmentsByResourceId(updatedAccessUser);

        assertThat(accessUserRepository.findAll().size()).isEqualTo(1);
        assertThat(accessAssignmentRepository.findAll().size()).isEqualTo(0);
        assertThat(scopeRepository.findAll().size()).isEqualTo(0);
        assertThat(accessAssignmentRepository.countAssignmentScopesByScopeId(scope.getId())).isEqualTo(0);
        assertThat(scopeRepository.countScopeOrgUnitsByScopeId(scope.getId())).isEqualTo(0);
        assertThat(orgUnitRepository.findAll().size()).isEqualTo(2);

        assertThat(accessUserRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldDeleteScopesWhenAllOrgUnitsAreDeletedFromScope() {
        OrgUnit orgUnit1 = createOrgUnit("198", "org 1");
        OrgUnit orgUnit2 = createOrgUnit("153", "org 2");

        Scope scope = createScope("orgunit", List.of(orgUnit1, orgUnit2));

        AccessUser accessUser = createAccessUser();
        AccessRole accessRole = createAccessRole("ata", "Access to all");

        AccessAssignment accessAssignment = createAccessAssignment(List.of(scope), accessUser, accessRole);

        List<AccessAssignment> accessAssignments = new ArrayList<>();
        accessAssignments.add(accessAssignment);

        accessUser.setAccessAssignments(accessAssignments);
        AccessUser updatedAccessUser = accessUserRepository.save(accessUser);

        verifyDataInserted(accessUser);

        accessAssignmentService.deleteOrgUnitFromScope(scope.getId(), orgUnit1.getOrgUnitId());
        accessAssignmentService.deleteOrgUnitFromScope(scope.getId(), orgUnit2.getOrgUnitId());

        assertThat(accessUserRepository.findAll().size()).isEqualTo(1);
        assertThat(scopeRepository.countScopeOrgUnitsByScopeId(scope.getId())).isEqualTo(0);
        assertThat(scopeRepository.findAll().size()).isEqualTo(0);
        assertThat(accessAssignmentRepository.findAll().size()).isEqualTo(1);
        assertThat(accessAssignmentRepository.countAssignmentScopesByScopeId(scope.getId())).isEqualTo(0);
        assertThat(orgUnitRepository.findAll().size()).isEqualTo(2);

        assertThat(accessUserRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldNotDeleteScopeIfThereAreMoreOrgunits() {
        OrgUnit orgUnit1 = createOrgUnit("198", "org 1");
        OrgUnit orgUnit2 = createOrgUnit("153", "org 2");

        Scope scope = createScope("orgunit", List.of(orgUnit1, orgUnit2));

        AccessUser accessUser = createAccessUser();
        AccessRole accessRole = createAccessRole("ata", "Access to all");

        AccessAssignment accessAssignment = createAccessAssignment(List.of(scope), accessUser, accessRole);

        List<AccessAssignment> accessAssignments = new ArrayList<>();
        accessAssignments.add(accessAssignment);

        accessUser.setAccessAssignments(accessAssignments);
        AccessUser updatedAccessUser = accessUserRepository.save(accessUser);

        verifyDataInserted(accessUser);

        accessAssignmentService.deleteOrgUnitFromScope(scope.getId(), orgUnit1.getOrgUnitId());

        assertThat(accessUserRepository.findAll().size()).isEqualTo(1);
        assertThat(scopeRepository.countScopeOrgUnitsByScopeId(scope.getId())).isEqualTo(1);
        assertThat(scopeRepository.findAll().size()).isEqualTo(1);
        assertThat(accessAssignmentRepository.findAll().size()).isEqualTo(1);
        assertThat(accessAssignmentRepository.countAssignmentScopesByScopeId(scope.getId())).isEqualTo(1);
        assertThat(orgUnitRepository.findAll().size()).isEqualTo(2);

        assertThat(accessUserRepository.count()).isEqualTo(1);
    }

    private void verifyDataInserted(AccessUser accessUser) {
        assertThat(accessUserRepository.findAll().size()).isEqualTo(1);
        assertThat(accessAssignmentRepository.findAll().size()).isEqualTo(1);
        assertThat(scopeRepository.findAll().size()).isEqualTo(1);
        assertThat(orgUnitRepository.findAll().size()).isEqualTo(2);

        accessUserRepository.findById(accessUser.getResourceId()).ifPresent(foundAccessUser -> {
            assertThat(foundAccessUser.getAccessAssignments()).hasSize(1);
            assertThat(foundAccessUser.getAccessAssignments().get(0).getScopes()).hasSize(1);
        });
    }

    private AccessUser createAccessUser() {
        AccessUser accessUser = AccessUser.builder()
                .resourceId("123")
                .userId("123")
                .userName("morten.solberg@vigoiks.no")
                .build();

        return accessUserRepository.save(accessUser);
    }

    private AccessAssignment createAccessAssignment(List<Scope> scopes, AccessUser accessUser, AccessRole accessRole) {
        AccessAssignment accessAssignment = AccessAssignment.builder()
                .accessRole(accessRole)
                .accessUser(accessUser)
                .scopes(scopes)
                .build();

        return accessAssignmentRepository.save(accessAssignment);
    }

    private Scope createScope(String scope, List<OrgUnit> orgUnits) {
        Scope orgUnitScope = Scope.builder()
                .objectType(scope)
                .orgUnits(orgUnits)
                .build();

        return scopeRepository.save(orgUnitScope);
    }

    private OrgUnit createOrgUnit(String orgUnitId, String name) {
        OrgUnit orgUnit = OrgUnit.builder()
                .name(name)
                .orgUnitId(orgUnitId)
                .build();
        return orgUnitRepository.save(orgUnit);
    }

    private AccessRole createAccessRole(String accessRoleId, String name) {
        AccessRole accessRole = AccessRole.builder()
                .accessRoleId(accessRoleId)
                .name(name)
                .build();

        return accessRoleRepository.save(accessRole);
    }
}
