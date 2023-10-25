package no.fintlabs.opa;

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
}
