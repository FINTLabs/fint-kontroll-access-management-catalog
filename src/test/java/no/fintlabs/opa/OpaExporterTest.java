package no.fintlabs.opa;

import no.fintlabs.accessassignment.repository.AccessAssignment;
import no.fintlabs.accesspermission.repository.AccessPermission;
import no.fintlabs.accesspermission.repository.AccessPermissionRepository;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.accessrole.AccessRoleRepository;
import no.fintlabs.feature.repository.Feature;
import no.fintlabs.orgunit.repository.OrgUnit;
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
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static no.fintlabs.user.AccessAssignmentMother.createAccessAssignmentWithScope;
import static no.fintlabs.user.AccessRoleMother.createAccessRole;
import static no.fintlabs.user.AccessUserMother.createAccessUser;
import static no.fintlabs.user.ScopeMother.createScope;
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
        AccessUser user = createAccessUser("1", "erling.jahr@vigoiks.no");

        OrgUnit orgUnit198 = new OrgUnit("198", "name1", "shortname1");
        OrgUnit orgUnit153 = new OrgUnit("153", "name2", "shortname2");
        OrgUnit orgUnit6 = new OrgUnit("6", "name3", "shortname3");
        OrgUnit orgUnit1 = new OrgUnit("1", "name4", "shortname4");

        List<OrgUnit> orgs1 = List.of(orgUnit198, orgUnit153);
        List<OrgUnit> orgs2 = List.of(orgUnit6, orgUnit1);

        AccessRole ata = createAccessRole("ata", "applikasjonstilgangsadministrator");

        AccessAssignment accessAssignmentScope1 = createAccessAssignmentWithScope(user, createScope(1L, "user", orgs1), ata);
        AccessAssignment accessAssignmentScope2 = createAccessAssignmentWithScope(user, createScope(2L, "role", orgs2), ata);

        user.setAccessAssignments(List.of(accessAssignmentScope1, accessAssignmentScope2));

        return user;
    }

    private AccessUser createMockAccessUserMorten() {
        AccessUser user = createAccessUser("1", "morten.solberg@vigoiks.no");

        OrgUnit orgUnit198 = new OrgUnit("3", "name1", "shortname1");
        OrgUnit orgUnit153 = new OrgUnit("8", "name2", "shortname2");
        OrgUnit orgUnit6 = new OrgUnit("44", "name3", "shortname3");
        OrgUnit orgUnit1 = new OrgUnit("11", "name4", "shortname4");

        List<OrgUnit> orgs1 = List.of(orgUnit198, orgUnit153);
        List<OrgUnit> orgs2 = List.of(orgUnit6, orgUnit1);

        AccessRole aa = createAccessRole("aa", "Applikasjonsadministrator");

        AccessAssignment accessAssignmentScope1 = createAccessAssignmentWithScope(user, createScope(3L, "resource", orgs1), aa);
        AccessAssignment accessAssignmentScope2 = createAccessAssignmentWithScope(user, createScope(4L, "license", orgs2), aa);

        user.setAccessAssignments(List.of(accessAssignmentScope1, accessAssignmentScope2));

        return user;
    }

}

