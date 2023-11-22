package no.fintlabs.opa.mapper;

import no.fintlabs.opa.dto.AssignmentScopeDto;
import no.fintlabs.orgunit.repository.OrgUnit;
import no.fintlabs.scope.repository.Scope;

import java.util.stream.Collectors;

public class AssignmentScopeMapper {

    /*public static AssignmentScopeDto mapAccessAssignmentToAssignmentScopeDto(AccessAssignment assignment) {
        AssignmentScopeDto scopeDto = new AssignmentScopeDto();
        scopeDto.setId(assignment.getScope().getId().toString());
        scopeDto.setObjecttype(assignment.getScope().getObjectType());
        scopeDto.setOrgunits(assignment.getScope().getScopeOrgUnits().stream()
                                     .map(orgUnit -> orgUnit.getOrgUnit().getOrgUnitId())
                                     .collect(Collectors.toList()));
        return scopeDto;
    }*/

    public static AssignmentScopeDto toDto(Scope scope) {
        AssignmentScopeDto scopeDto = new AssignmentScopeDto();
        scopeDto.setId(scope.getId().toString());
        scopeDto.setObjecttype(scope.getObjectType());
        scopeDto.setOrgunits(scope.getOrgUnits().stream()
                                     .map(OrgUnit::getOrgUnitId)
                                     .collect(Collectors.toList()));
        return scopeDto;
    }
}
