package no.fintlabs.opa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RolesAndScopesDto {

    private List<RoleAndScopeDto> rolesandscopes;
}
