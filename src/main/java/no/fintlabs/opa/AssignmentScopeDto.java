package no.fintlabs.opa;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignmentScopeDto {

    private String id;
    private String objecttype;
    private List<String> orgunits;
}
