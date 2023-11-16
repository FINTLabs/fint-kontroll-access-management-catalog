package no.fintlabs.opa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OpaController.class)
class OpaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpaBundleService opaBundleService;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new OpaController(opaBundleService)).build();
    }

    @Test
    public void shouldGetOkForOpaBundle() throws Exception {
        Resource urlResource = mock(Resource.class);

        when(urlResource.exists()).thenReturn(true);
        when(urlResource.getFilename()).thenReturn("opabundle.tar.gz");

        when(opaBundleService.getOpaBundleFile()).thenReturn(urlResource);

        mockMvc.perform(get("/api/accessmanagement/v1/opabundle")
                                .accept("application/gzip"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGet404IfNotFound() throws Exception {
        Resource urlResource = mock(Resource.class);

        when(urlResource.exists()).thenReturn(false);
        when(opaBundleService.getOpaBundleFile()).thenReturn(urlResource);

        mockMvc.perform(get("/api/accessmanagement/v1/opabundle")
                                .accept("application/gzip"))
                .andExpect(status().isNotFound());
    }
}
