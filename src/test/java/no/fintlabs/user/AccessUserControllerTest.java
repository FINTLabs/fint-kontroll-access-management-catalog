package no.fintlabs.user;

import no.fintlabs.SecurityConfig;
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
    public void shouldGetAllUsers() throws Exception {
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
}
