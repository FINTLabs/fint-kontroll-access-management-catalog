package no.fintlabs.accessassignment;

import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.accessrole.AccessRoleRepository;
import no.fintlabs.user.AccessRoleMother;
import no.fintlabs.user.AccessUserMother;
import no.fintlabs.user.repository.AccessUser;
import no.fintlabs.user.repository.AccessUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccessAssignmentController.class)
public class AccessAssignmentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccessAssignmentService accessAssignmentServiceMock;

    @MockBean
    private AccessUserRepository accessUserRepositoryMock;

    @MockBean
    private AccessRoleRepository accessRoleRepositoryMock;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new AccessAssignmentController(accessAssignmentServiceMock, accessUserRepositoryMock, accessRoleRepositoryMock)).build();
    }

    @Test
    public void shouldGetAccessAssignments() throws Exception {
        AccessUser user = AccessUserMother.createAccessUserWithAssignments("1234", "username1");
        user.getAccessAssignments().get(0).getAccessUser().setResourceId("1234");

        when(accessUserRepositoryMock.findByResourceId("1234")).thenReturn(user);

        mockMvc.perform(get("/api/accessmanagement/v1/accessassignment/user/{resourceId}", "1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("999"))
                .andExpect(jsonPath("$[0].accessRoleId").value("ata"))
                .andExpect(jsonPath("$[0].scopes").isArray())
                .andExpect(jsonPath("$[0].userId").value("1234"));
    }

    @Test
    public void shouldDeleteAccessAssignmentByRoleAndObjectType() throws Exception {
        AccessUser user = AccessUserMother.createDefaultAccessUser();
        AccessRole role = AccessRoleMother.createDefaultAccessRole();

        when(accessUserRepositoryMock.findByResourceId("2")).thenReturn(user);
        when(accessRoleRepositoryMock.findById("ata")).thenReturn(Optional.ofNullable(role));

        mockMvc.perform(delete("/api/accessmanagement/v1/accessassignment/user/{resourceId}/role/{roleId}", "2", "ata")
                                .queryParam("objectType", "orgunit"))
                .andExpect(status().isNoContent());

        verify(accessAssignmentServiceMock, times(1)).deleteAllAssignmentsByResourceIdAndRoleAndObjectType(user, role, "orgunit");
    }

    @Test
    public void shouldDeleteAccessAssignmentsForUser() throws Exception {
        AccessUser user = AccessUserMother.createDefaultAccessUser();

        when(accessUserRepositoryMock.findByResourceId("2")).thenReturn(user);

        mockMvc.perform(delete("/api/accessmanagement/v1/accessassignment/user/{resourceId}", "2"))
                .andExpect(status().isNoContent());

        verify(accessAssignmentServiceMock, times(1)).deleteAllAssignmentsByResourceId(user);
    }

    @Test
    public void shouldDeleteOrgUnitFromScope() throws Exception {
        mockMvc.perform(delete("/api/accessmanagement/v1/accessassignment/scope/{scopeId}/orgunit/{orgUnitId}", "1", "198"))
                .andExpect(status().isNoContent());

        verify(accessAssignmentServiceMock, times(1)).deleteOrgUnitFromScope(1L, "198");
    }


}
