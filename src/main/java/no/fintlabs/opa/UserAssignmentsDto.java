package no.fintlabs.opa;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UserAssignmentsDto {

    private Map<String, List<RolesAndScopesDto>> user_assignments = new HashMap<>();
}
