package no.fintlabs.opa;

import no.fintlabs.accessassignment.repository.AccessAssignment;
import no.fintlabs.accessassignment.repository.AccessAssignmentId;
import no.fintlabs.accesspermission.repository.AccessPermission;
import no.fintlabs.accesspermission.repository.AccessPermissionRepository;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.accessrole.AccessRoleRepository;
import no.fintlabs.feature.repository.Feature;
import no.fintlabs.orgunit.repository.OrgUnit;
import no.fintlabs.scope.repository.Scope;
import no.fintlabs.scope.repository.ScopeOrgUnit;
import no.fintlabs.scope.repository.ScopeOrgUnitId;
import no.fintlabs.user.repository.AccessUser;
import no.fintlabs.user.repository.AccessUserRepository;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OpaExporterTest {

    @Mock
    private AccessUserRepository accessUserRepository;

    @Mock
    private AccessPermissionRepository accessPermissionRepository;

    @Mock
    private AccessRoleRepository accessRoleRepository;

    @Mock
    private OpaBundleService opaBundleService;

    @InjectMocks
    private OpaExporter dataExporter;

    @Test
    public void shouldMapObjectDataToOpaJson() throws Exception {
        AccessUser userErling = createMockAccessUserErling();
        AccessUser userMorten = createMockAccessUserMorten();

        AccessPermission accessPermission = createAccessPermission("/api/feature", "GET");
        AccessPermission accessPermission2 = createAccessPermission("/api/feature2", "POST");

        when(accessUserRepository.findAll()).thenReturn(List.of(userErling, userMorten));
        when(accessPermissionRepository.findAll()).thenReturn(List.of(accessPermission, accessPermission2));
        when(accessRoleRepository.findAll()).thenReturn(List.of(AccessRole.builder().accessRoleId("ata").name("applikasjonstilgangsadministrator").build()));

        dataExporter.setOpaFile("output.json");
        dataExporter.exportOpaDataToJson();

        String expectedJson = IOUtils.toString(getClass().getResourceAsStream("/expected_output_to_opa.json"), UTF_8.name());

        Path mappedOpaJsonFile = Path.of(System.getProperty("java.io.tmpdir"), "/opa/datacatalog/", "output.json");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode resultNode = mapper.readTree(new String(Files.readAllBytes(mappedOpaJsonFile)));
        JsonNode expectedNode = mapper.readTree(expectedJson);

        assertEquals(expectedNode, resultNode);
    }

    @NotNull
    private AccessPermission createAccessPermission(String path, String GET) {
        Feature feature = new Feature();
        feature.setPath(path);

        AccessPermission accessPermission = new AccessPermission();
        accessPermission.setAccessRoleId("ata");
        accessPermission.setOperation(GET);
        accessPermission.setFeature(feature);
        return accessPermission;
    }

    private AccessUser createMockAccessUserErling() {
        AccessUser userErling = createAccessUser("1", "erling.jahr@vigoiks.no");

        AccessAssignment accessAssignmentScope1Erling = createAssignment(1L, "ata", userErling);
        AccessAssignment accessAssignmentScope2Erling = createAssignment(2L, "ata", userErling);
        AccessAssignment accessAssignmentScope3Erling = createAssignment(3L, "ata", userErling);
        AccessAssignment accessAssignmentScope4Erling = createAssignment(4L, "ata", userErling);

        OrgUnit orgUnit198 = new OrgUnit("198", "name1", "shortname1", null, null);
        OrgUnit orgUnit153 = new OrgUnit("153", "name2", "shortname2", null, null);
        OrgUnit orgUnit6 = new OrgUnit("6", "name3", "shortname3", null, null);
        OrgUnit orgUnit1 = new OrgUnit("1", "name4", "shortname4", null, null);

        Scope scopeUser = new Scope(1L, "user", null);
        Scope scopeRole = new Scope(2L, "role", null);
        Scope scopeResource = new Scope(3L, "resource", null);
        Scope scopeOrgUnit = new Scope(4L, "orgunit", null);

        List<ScopeOrgUnit> scopeOrgunitsOrg = Arrays.asList(
                new ScopeOrgUnit(new ScopeOrgUnitId(1L, orgUnit198.getOrgUnitId()), scopeUser, orgUnit198),
                new ScopeOrgUnit(new ScopeOrgUnitId(2L, orgUnit153.getOrgUnitId()), scopeRole, orgUnit153),
                new ScopeOrgUnit(new ScopeOrgUnitId(3L, orgUnit6.getOrgUnitId()), scopeResource, orgUnit6),
                new ScopeOrgUnit(new ScopeOrgUnitId(4L, orgUnit1.getOrgUnitId()), scopeOrgUnit, orgUnit1)
        );

        scopeUser.setScopeOrgUnits(scopeOrgunitsOrg);
        scopeRole.setScopeOrgUnits(scopeOrgunitsOrg);
        scopeResource.setScopeOrgUnits(scopeOrgunitsOrg);
        scopeOrgUnit.setScopeOrgUnits(scopeOrgunitsOrg);

        orgUnit198.setScopeOrgUnits(scopeOrgunitsOrg);
        orgUnit153.setScopeOrgUnits(scopeOrgunitsOrg);
        orgUnit6.setScopeOrgUnits(scopeOrgunitsOrg);
        orgUnit1.setScopeOrgUnits(scopeOrgunitsOrg);

        accessAssignmentScope1Erling.setScope(scopeUser);
        accessAssignmentScope2Erling.setScope(scopeRole);
        accessAssignmentScope3Erling.setScope(scopeResource);
        accessAssignmentScope4Erling.setScope(scopeOrgUnit);

        userErling.setAccessAssignments(Arrays.asList(accessAssignmentScope1Erling, accessAssignmentScope2Erling, accessAssignmentScope3Erling, accessAssignmentScope4Erling));

        return userErling;
    }

    private AccessUser createMockAccessUserMorten() {
        AccessUser userMorten = createAccessUser("2", "morten.solberg@vigoiks.no");

        AccessAssignment accessAssignmentScope1Morten = createAssignment(1, "aa", userMorten);
        AccessAssignment accessAssignmentScope2Morten = createAssignment(2, "aa", userMorten);

        OrgUnit orgUnit198 = new OrgUnit("198", "name1", "shortname1", null, null);
        OrgUnit orgUnit153 = new OrgUnit("153", "name2", "shortname2", null, null);
        OrgUnit orgUnit6 = new OrgUnit("6", "name3", "shortname3", null, null);
        OrgUnit orgUnit1 = new OrgUnit("1", "name4", "shortname4", null, null);

        Scope scopeUser = new Scope(1L, "user", null);
        Scope scopeRole = new Scope(2L, "role", null);

        List<ScopeOrgUnit> scopeOrgunitsOrg = Arrays.asList(
                new ScopeOrgUnit(new ScopeOrgUnitId(1L, orgUnit198.getOrgUnitId()), scopeUser, orgUnit198),
                new ScopeOrgUnit(new ScopeOrgUnitId(1L, orgUnit153.getOrgUnitId()), scopeUser, orgUnit153),
                new ScopeOrgUnit(new ScopeOrgUnitId(1L, orgUnit6.getOrgUnitId()), scopeUser, orgUnit6),
                new ScopeOrgUnit(new ScopeOrgUnitId(1L, orgUnit1.getOrgUnitId()), scopeUser, orgUnit1)
        );

        List<ScopeOrgUnit> scopeOrgunitsRole = Arrays.asList(
                new ScopeOrgUnit(new ScopeOrgUnitId(2L, orgUnit198.getOrgUnitId()), scopeUser, orgUnit198),
                new ScopeOrgUnit(new ScopeOrgUnitId(2L, orgUnit153.getOrgUnitId()), scopeUser, orgUnit153)
                );

        scopeUser.setScopeOrgUnits(scopeOrgunitsOrg);
        scopeRole.setScopeOrgUnits(scopeOrgunitsRole);

        accessAssignmentScope1Morten.setScope(scopeUser);
        accessAssignmentScope2Morten.setScope(scopeRole);

        userMorten.setAccessAssignments(Arrays.asList(accessAssignmentScope1Morten, accessAssignmentScope2Morten));

        return userMorten;
    }

    private AccessAssignment createAssignment(long scopeId, String roleId, AccessUser mockUser) {
        return AccessAssignment.builder()
                .accessAssignmentId(new AccessAssignmentId(scopeId, roleId, mockUser.getUserId()))
                .accessUser(mockUser)
                .build();
    }

    private AccessUser createAccessUser(String userId, String userName) {
        return AccessUser.builder()
                .userId(userId)
                .userName(userName)
                .build();
    }
}

