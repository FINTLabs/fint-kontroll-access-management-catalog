package no.fintlabs.user;

import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.orgunit.OrgUnitDto;
import no.fintlabs.scope.repository.Scope;
import no.fintlabs.scope.repository.ScopeOrgUnit;
import no.fintlabs.user.dto.AccessUserDto;
import no.fintlabs.user.dto.AccessUserScopesDto;
import no.fintlabs.user.dto.UserRoleDto;
import no.fintlabs.user.repository.AccessUser;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccessUserMapperTest {

    @Test
    void shouldMapUserWithNoAssignments() {
        AccessUser accessUser = AccessUser.builder()
                .resourceId("1")
                .userId("user1")
                .userName("username")
                .firstName("FirstName")
                .lastName("LastName")
                .accessAssignments(Collections.emptyList())
                .build();

        AccessUserDto result = AccessUserMapper.toDto(accessUser);

        assertTrue(result.roles().isEmpty(), "Roles should be empty");
    }

    @Test
    void shouldMapUserWithAssignments() {
        AccessUser accessUser = AccessUser.builder()
                .resourceId("1")
                .userId("user1")
                .userName("username")
                .firstName("FirstName")
                .lastName("LastName")
                .accessAssignments(Collections.singletonList(AccessAssignmentMother.createDefaultAccessAssignment()))
                .build();

        AccessUserDto result = AccessUserMapper.toDto(accessUser);

        assertThat(result.resourceId()).isEqualTo(accessUser.getResourceId());
        assertThat(result.userId()).isEqualTo(accessUser.getUserId());
        assertThat(result.userName()).isEqualTo(accessUser.getUserName());
        assertThat(result.firstName()).isEqualTo(accessUser.getFirstName());
        assertThat(result.lastName()).isEqualTo(accessUser.getLastName());
        assertFalse(result.roles().isEmpty(), "Roles should not be empty");

        UserRoleDto actualUserRole = result.roles().get(0);
        AccessRole expectedUserRole = accessUser.getAccessAssignments().get(0).getAccessRole();

        assertThat(actualUserRole.roleId()).isEqualTo(expectedUserRole.getAccessRoleId());
        assertThat(actualUserRole.roleName()).isEqualTo(expectedUserRole.getName());
        assertFalse(actualUserRole.scopes().isEmpty(), "Scopes should not be empty");

        AccessUserScopesDto actualUserScope = actualUserRole.scopes().get(0);
        Scope expectedUserScope = accessUser.getAccessAssignments().get(0).getScope();

        assertThat(actualUserScope.scopeId()).isEqualTo(expectedUserScope.getId());
        assertThat(actualUserScope.objectType()).isEqualTo(expectedUserScope.getObjectType());
        assertFalse(actualUserScope.orgUnits().isEmpty(), "OrgUnits should not be empty");

        OrgUnitDto actualOrgUnit = actualUserScope.orgUnits().get(0);
        ScopeOrgUnit expectedOrgUnit = expectedUserScope.getScopeOrgUnits().get(0);

        assertThat(actualOrgUnit.orgUnitId()).isEqualTo(expectedOrgUnit.getOrgUnit().getOrgUnitId());
        assertThat(actualOrgUnit.name()).isEqualTo(expectedOrgUnit.getOrgUnit().getName());

    }
}


