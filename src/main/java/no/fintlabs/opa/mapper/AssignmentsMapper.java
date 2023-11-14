package no.fintlabs.opa.mapper;

import no.fintlabs.opa.dto.RolesAndScopesDto;
import no.fintlabs.user.repository.AccessUser;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignmentsMapper {

    public static Map<String, List<RolesAndScopesDto>> mapUserAssignments(List<AccessUser> users) {
        Map<String, List<RolesAndScopesDto>> userAssignments = new HashMap<>();

        for (AccessUser user : users) {
            RolesAndScopesDto rolesAndScopesWrapper = RolesAndScopesMapper.mapAccessUserToRolesAndScopesDto(user);
            userAssignments.put(user.getUserName(), Collections.singletonList(rolesAndScopesWrapper));
        }
        return userAssignments;
    }
}
