package no.fintlabs.opa.mapper;

import no.fintlabs.accessassignment.AccessAssignment;
import no.fintlabs.opa.dto.AssignmentScopeDto;

import java.util.stream.Collectors;

public class AssignmentScopeMapper {

    public static AssignmentScopeDto mapAccessAssignmentToAssignmentScopeDto(AccessAssignment assignment) {
        AssignmentScopeDto scopeDto = new AssignmentScopeDto();
        scopeDto.setId(assignment.getScope().getId().toString());
        scopeDto.setObjecttype(assignment.getScope().getObjectType());
        scopeDto.setOrgunits(assignment.getScope().getScopeOrgUnits().stream()
                                     .map(orgUnit -> orgUnit.getOrgUnit().getOrgUnitId())
                                     .collect(Collectors.toList()));
        return scopeDto;
    }
}
