package no.fintlabs.feature;

import no.fintlabs.SecurityConfig;
import no.fintlabs.feature.repository.Feature;
import no.fintlabs.feature.repository.FeatureRepository;
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

@WebMvcTest(FeatureController.class)
@Import({SecurityConfig.class})
public class FeatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeatureRepository featureRepositoryMock;

    @WithMockUser(value = "spring")
    @Test
    public void shouldGetAllFeatures() throws Exception {
        Feature feature = Feature.builder()
                .id(1L)
                .name("Feature 1")
                .path("/api/orgunit")
                .build();

        when(featureRepositoryMock.findAll()).thenReturn(List.of(feature));

        mockMvc.perform(get("/api/accessmanagement/v1/feature"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Feature 1"))
                .andExpect(jsonPath("$[0].path").value("/api/orgunit"));
    }

    @WithMockUser(value = "spring")
    @Test
    public void shouldGetFeaturesByRole() throws Exception {
        AccessRoleFeature feature = new AccessRoleFeature("Feature 1", "/api/orgunit", "GET");

        when(featureRepositoryMock.findFeaturesByAccessRoleId("ata")).thenReturn(List.of(feature));

        mockMvc.perform(get("/api/accessmanagement/v1/feature/ata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].name").value("Feature 1"))
                .andExpect(jsonPath("$[0].path").value("/api/orgunit"))
                .andExpect(jsonPath("$[0].operation").value("GET"));
    }
}
