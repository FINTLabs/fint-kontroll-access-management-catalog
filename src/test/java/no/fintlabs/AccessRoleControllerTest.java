package no.fintlabs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest
@Import({SecurityConfig.class})
public class AccessRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser(value = "spring")
    @Test
    public void shouldGetAccessRoles() throws Exception {
        mockMvc.perform(get("/api/accessmanagement/v1/accessrole"))
                .andExpect(status().isOk())
                .andExpect(
                        content().json("['APPLIKASJONSTILGANGSADMINISTRATOR', 'APPLIKASJONSADMINISTRATOR', 'ENHETSLEDER', 'SLUTTBRUKER']"));
    }
}
