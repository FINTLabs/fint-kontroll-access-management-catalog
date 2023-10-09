package no.fintlabs.accessrole;

import no.fintlabs.SecurityConfig;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.accessrole.AccessRoleController;
import no.fintlabs.accessrole.AccessRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(AccessRoleController.class)
@Import({SecurityConfig.class})
public class AccessRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccessRoleRepository accessRoleRepository;

    @WithMockUser(value = "spring")
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
