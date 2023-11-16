package no.fintlabs.accessrole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccessRoleController.class)
public class AccessRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccessRoleRepository accessRoleRepository;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new AccessRoleController(accessRoleRepository)).build();
    }

    @Test
    public void shouldGetAccessRoles() throws Exception {
        AccessRole accessRole = AccessRole.builder()
                .accessRoleId("ata")
                .name("applikasjonstilgangsadministrator")
                .build();

        when(accessRoleRepository.findAll()).thenReturn(List.of(accessRole));

        mockMvc.perform(get("/api/accessmanagement/v1/accessrole"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].accessRoleId").value("ata"))
                .andExpect(jsonPath("$[0].name").value("applikasjonstilgangsadministrator"));
    }
}
