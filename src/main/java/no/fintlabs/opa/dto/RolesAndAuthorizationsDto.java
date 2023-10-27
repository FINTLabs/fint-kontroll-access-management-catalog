package no.fintlabs.opa.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class RolesAndAuthorizationsDto {
    private Map<String, List<RoleDto>> roles;
}
