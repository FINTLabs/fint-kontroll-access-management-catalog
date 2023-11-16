package no.fintlabs.accesspermission;

import no.fintlabs.accesspermission.repository.AccessPermission;
import no.fintlabs.accesspermission.repository.AccessPermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccessPermissionController.class)
public class AccessPermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccessPermissionRepository accessPermissionRepositoryMock;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new AccessPermissionController(accessPermissionRepositoryMock)).build();
    }

    @Test
    public void shouldCreateAccessPermission() throws Exception {
        AccessPermissionDto inputAccessPermissionDto = new AccessPermissionDto("ata", 1L, "GET");
        AccessPermission savedAccessPermission = AccessPermission.builder()
                .id(1L)
                .accessRoleId("ata")
                .featureId(1L)
                .operation("GET")
                .build();

        when(accessPermissionRepositoryMock.save(any(AccessPermission.class))).thenReturn(savedAccessPermission);

        mockMvc.perform(post("/api/accessmanagement/v1/accesspermission")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(inputAccessPermissionDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.accessRoleId").value("ata"))
                .andExpect(jsonPath("$.featureId").value("1"))
                .andExpect(jsonPath("$.operation").value("GET"));
    }
}
