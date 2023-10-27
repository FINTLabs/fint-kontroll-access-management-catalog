package no.fintlabs.opa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RoleAndScopeDto {

    private String role;
    private List<AssignmentScopeDto> scopes;
}
