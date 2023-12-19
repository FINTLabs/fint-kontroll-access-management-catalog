package no.fintlabs.user;

import no.fintlabs.orgunit.repository.OrgUnitInfo;
import no.fintlabs.orgunit.repository.OrgUnitRepository;
import no.fintlabs.user.repository.AccessUser;
import no.fintlabs.user.repository.AccessUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccessUserController.class)
public class AccessUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccessUserRepository accessUserRepositoryMock;

    @MockBean
    private OrgUnitRepository orgUnitRepositoryMock;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new AccessUserController(accessUserRepositoryMock, orgUnitRepositoryMock))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

    }

    @Test
    public void shouldGetAllUsersNoAssignments() throws Exception {
        AccessUser accessUser = AccessUser.builder()
                .resourceId("1")
                .userId("1")
                .userName("morten.solberg@vigoiks.no")
                .firstName("Morten")
                .lastName("Solberg")
                .build();

        Page<AccessUser> accessUserPage = new PageImpl<>(List.of(accessUser));

        when(accessUserRepositoryMock.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(accessUserPage);

        mockMvc.perform(get("/api/accessmanagement/v1/user")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "userName,asc")
                                .param("name", "")
                                .param("orgunitid", "")
                                .param("accessroleid", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.totalItems").value("1"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.currentPage").value("0"))
                .andExpect(jsonPath("$.users[0].resourceId").value("1"))
                .andExpect(jsonPath("$.users[0].userName").value("morten.solberg@vigoiks.no"))
                .andExpect(jsonPath("$.users[0].firstName").value("Morten"))
                .andExpect(jsonPath("$.users[0].lastName").value("Solberg"));
    }


    @Test
    public void shouldGetAllUsersWithAssignments() throws Exception {
        AccessUser accessUser = AccessUser.builder()
                .resourceId("1")
                .userId("1")
                .userName("morten.solberg@vigoiks.no")
                .firstName("Morten")
                .lastName("Solberg")
                .accessAssignments(Collections.singletonList(AccessAssignmentMother.createDefaultAccessAssignment()))
                .build();

        Page<AccessUser> accessUserPage = new PageImpl<>(List.of(accessUser));

        when(accessUserRepositoryMock.findAll(any(Specification.class), any(Pageable.class))).thenReturn(accessUserPage);

        String user = "$.users[0]";
        String roles = user + ".roles[0]";
        String scopes = roles + ".scopes";

        mockMvc.perform(get("/api/accessmanagement/v1/user")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "userName,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.totalItems").value("1"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.currentPage").value("0"))
                .andExpect(jsonPath(user + ".resourceId").value("1"))
                .andExpect(jsonPath(user + ".userName").value("morten.solberg@vigoiks.no"))
                .andExpect(jsonPath(user + ".firstName").value("Morten"))
                .andExpect(jsonPath(user + ".lastName").value("Solberg"))
                .andExpect(jsonPath(user + ".roles").isArray())
                .andExpect(jsonPath(roles + ".roleId").value("ata"))
                .andExpect(jsonPath(roles + ".roleName").value("Applikasjonstilgangsadministrator"));
    }

    @Test
    public void shouldGetUserWithAssignments() throws Exception {
        AccessUser accessUser = AccessUser.builder()
                .resourceId("1")
                .userId("1")
                .userName("morten.solberg@vigoiks.no")
                .firstName("Morten")
                .lastName("Solberg")
                .accessAssignments(Collections.singletonList(AccessAssignmentMother.createDefaultAccessAssignment()))
                .build();

        when(accessUserRepositoryMock.findById("1")).thenReturn(Optional.ofNullable(accessUser));

        mockMvc.perform(get("/api/accessmanagement/v1/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resourceId").value("1"))
                .andExpect(jsonPath("$.userName").value("morten.solberg@vigoiks.no"))
                .andExpect(jsonPath("$.firstName").value("Morten"))
                .andExpect(jsonPath("$.lastName").value("Solberg"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0].roleId").value("ata"))
                .andExpect(jsonPath("$.roles[0].roleName").value("Applikasjonstilgangsadministrator"));
    }

    @Test
    public void shouldGetOrgUnitsForUser() throws Exception {
        when(orgUnitRepositoryMock.findOrgUnitsForUserByFilters(any(), any(), any(), any(), any())).thenReturn(
                new PageImpl<>(List.of(new OrgUnitInfo("ata", 1L, "orgunit", "name1", "198"))));

        mockMvc.perform(get("/api/accessmanagement/v1/user/1/orgunits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value("1"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.currentPage").value("0"))
                .andExpect(jsonPath("$.accessRoles").isArray())
                .andExpect(jsonPath("$.accessRoles[0].accessRoleId").value("ata"))
                .andExpect(jsonPath("$.accessRoles[0].orgUnits").isArray())
                .andExpect(jsonPath("$.accessRoles[0].orgUnits[0].name").value("name1"))
                .andExpect(jsonPath("$.accessRoles[0].orgUnits[0].objectType").value("orgunit"))
                .andExpect(jsonPath("$.accessRoles[0].orgUnits[0].scopeId").value("1"))
                .andExpect(jsonPath("$.accessRoles[0].orgUnits[0].orgUnitId").value("198"));
    }

    @Test
    public void shouldGetOrgUnitsForUserWithFilters() throws Exception {
        when(orgUnitRepositoryMock.findOrgUnitsForUserByFilters(any(), any(), any(), any(), any())).thenReturn(
                new PageImpl<>(List.of(new OrgUnitInfo("ata", 1L, "orgunit", "name1", "198"))));

        mockMvc.perform(get("/api/accessmanagement/v1/user/1/orgunits")
                                .param("accessroleid", "ata")
                                .param("objecttype", "orgunit")
                                .param("orgunitname", "name1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value("1"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.currentPage").value("0"))
                .andExpect(jsonPath("$.accessRoles").isArray())
                .andExpect(jsonPath("$.accessRoles[0].accessRoleId").value("ata"))
                .andExpect(jsonPath("$.accessRoles[0].orgUnits").isArray())
                .andExpect(jsonPath("$.accessRoles[0].orgUnits[0].name").value("name1"))
                .andExpect(jsonPath("$.accessRoles[0].orgUnits[0].objectType").value("orgunit"))
                .andExpect(jsonPath("$.accessRoles[0].orgUnits[0].scopeId").value("1"))
                .andExpect(jsonPath("$.accessRoles[0].orgUnits[0].orgUnitId").value("198"));
    }
}
