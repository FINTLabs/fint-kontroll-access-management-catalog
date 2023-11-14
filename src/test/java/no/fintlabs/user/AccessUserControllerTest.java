package no.fintlabs.user;

import no.fintlabs.SecurityConfig;
import no.fintlabs.accessassignment.AccessAssignment;
import no.fintlabs.accessassignment.AccessAssignmentId;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.orgunit.OrgUnit;
import no.fintlabs.scope.Scope;
import no.fintlabs.scope.ScopeOrgUnit;
import no.fintlabs.scope.ScopeOrgUnitId;
import no.fintlabs.user.repository.AccessUser;
import no.fintlabs.user.repository.AccessUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccessUserController.class)
@Import({SecurityConfig.class})
public class AccessUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccessUserRepository accessUserRepositoryMock;

    @WithMockUser(value = "spring")
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

        when(accessUserRepositoryMock.findAll(any(Specification.class), any(Pageable.class))).thenReturn(accessUserPage);

        mockMvc.perform(get("/api/accessmanagement/v1/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.totalItems").value("1"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.currentPage").value("0"))
                .andExpect(jsonPath("$.users[0].resourceId").value("1"))
                .andExpect(jsonPath("$.users[0].userId").value("1"))
                .andExpect(jsonPath("$.users[0].userName").value("morten.solberg@vigoiks.no"))
                .andExpect(jsonPath("$.users[0].firstName").value("Morten"))
                .andExpect(jsonPath("$.users[0].lastName").value("Solberg"));
    }

    @WithMockUser(value = "spring")
    @Test
    public void shouldGetAllUsersWithAssignments() throws Exception {
        AccessUser accessUser = AccessUser.builder()
                .resourceId("1")
                .userId("1")
                .userName("morten.solberg@vigoiks.no")
                .firstName("Morten")
                .lastName("Solberg")
                .accessAssignments(List.of(AccessAssignment.builder()
                        .accessAssignmentId(AccessAssignmentId.builder()
                                .scopeId(1L)
                                .userId("1")
                                .accessRoleId("ata")
                                .build())
                        .scope(Scope.builder()
                                .objectType("orgunit")
                                .scopeOrgUnits(List.of(ScopeOrgUnit.builder()
                                        .scopeOrgUnitId(ScopeOrgUnitId.builder()
                                                .scopeId(1L)
                                                .orgUnitId("198")
                                                .build())
                                        .orgUnit(OrgUnit.builder()
                                                .orgUnitId("198")
                                                .name("OrgUnit1")
                                                .build())
                                        .build(), ScopeOrgUnit.builder()
                                        .scopeOrgUnitId(ScopeOrgUnitId.builder()
                                                .scopeId(1L)
                                                .orgUnitId("153")
                                                .build())
                                        .orgUnit(OrgUnit.builder()
                                                .orgUnitId("153")
                                                .name("OrgUnit2")
                                                .build())
                                        .build()))
                                .build())
                        .accessRole(AccessRole.builder()
                                .accessRoleId("ata")
                                .name("Applikasjonstilgangsadministrator")
                                .build())
                        .build()))
                .build();

        Page<AccessUser> accessUserPage = new PageImpl<>(List.of(accessUser));

        when(accessUserRepositoryMock.findAll(any(Specification.class), any(Pageable.class))).thenReturn(accessUserPage);

        String user = "$.users[0]";
        String roles = user + ".roles[0]";
        String scopes = roles + ".scopes";

        mockMvc.perform(get("/api/accessmanagement/v1/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.totalItems").value("1"))
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.currentPage").value("0"))
                .andExpect(jsonPath(user + ".resourceId").value("1"))
                .andExpect(jsonPath(user + ".userId").value("1"))
                .andExpect(jsonPath(user + ".userName").value("morten.solberg@vigoiks.no"))
                .andExpect(jsonPath(user + ".firstName").value("Morten"))
                .andExpect(jsonPath(user + ".lastName").value("Solberg"))
                .andExpect(jsonPath(user + ".roles").isArray())
                .andExpect(jsonPath(roles + ".roleId").value("ata"))
                .andExpect(jsonPath(roles + ".roleName").value("Applikasjonstilgangsadministrator"))
                .andExpect(jsonPath(scopes).isArray())
                .andExpect(jsonPath(scopes + "[0].scopeId").value(1L))
                .andExpect(jsonPath(scopes + "[0].objectType").value("orgunit"))
                .andExpect(jsonPath(scopes + "[0].orgUnits").isArray())
                .andExpect(jsonPath(scopes + "[0].orgUnits.[0].name").value("OrgUnit1"))
                .andExpect(jsonPath(scopes + "[0].orgUnits.[0].orgUnitId").value("198"));
    }
}
