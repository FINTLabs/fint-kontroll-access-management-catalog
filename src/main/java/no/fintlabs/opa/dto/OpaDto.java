package no.fintlabs.opa.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class OpaDto {

    private Map<String, List<RolesAndScopesDto>> user_assignments = new HashMap<>();
    private Map<String, List<RoleAuthorizationsDto>> role_authorizations = new HashMap<>();
    private Map<String, List<RoleDto>> roles = new HashMap<>();

    @JsonIgnore
    public byte[] getJsonAsBytes() throws JsonProcessingException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(this);
    }
}
