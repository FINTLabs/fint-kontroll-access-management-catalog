package no.fintlabs.user;

import no.fintlabs.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
    public void shouldGetAllUsers() throws Exception {
        AccessUser accessUser = AccessUser.builder()
                .resourceId("1")
                .userId("1")
                .userName("morten.solberg@vigoiks.no")
                .firstName("Morten")
                .lastName("Solberg")
                .build();

        when(accessUserRepositoryMock.findAll()).thenReturn(List.of(accessUser));

        mockMvc.perform(get("/api/accessmanagement/v1/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].resourceId").value("1"))
                .andExpect(jsonPath("$[0].userId").value("1"))
                .andExpect(jsonPath("$[0].userName").value("morten.solberg@vigoiks.no"))
                .andExpect(jsonPath("$[0].firstName").value("Morten"))
                .andExpect(jsonPath("$[0].lastName").value("Solberg"));
    }
}
