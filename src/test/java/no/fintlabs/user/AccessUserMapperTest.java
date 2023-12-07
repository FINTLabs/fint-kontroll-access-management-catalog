package no.fintlabs.user;

import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.orgunit.OrgUnitDto;
import no.fintlabs.orgunit.repository.OrgUnit;
import no.fintlabs.orgunit.repository.OrgUnitInfo;
import no.fintlabs.scope.repository.Scope;
import no.fintlabs.user.dto.AccessUserAccessRolesDto;
import no.fintlabs.user.dto.AccessUserDto;
import no.fintlabs.user.dto.AccessUserOrgUnitDto;
import no.fintlabs.user.dto.AccessUserOrgUnitsResponseDto;
import no.fintlabs.user.dto.AccessUserScopesDto;
import no.fintlabs.user.dto.UserRoleDto;
import no.fintlabs.user.repository.AccessUser;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        AccessUserDto result = AccessUserMapper.toAccessUserDto(accessUser);

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

        AccessUserDto result = AccessUserMapper.toAccessUserDto(accessUser);

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
        Scope expectedUserScope = accessUser.getAccessAssignments().get(0).getScopes().get(0);

        assertThat(actualUserScope.scopeId()).isEqualTo(expectedUserScope.getId());
        assertThat(actualUserScope.objectType()).isEqualTo(expectedUserScope.getObjectType());
        assertFalse(actualUserScope.orgUnits().isEmpty(), "OrgUnits should not be empty");

        OrgUnitDto actualOrgUnit = actualUserScope.orgUnits().get(0);
        OrgUnit expectedOrgUnit = expectedUserScope.getOrgUnits().get(0);

        assertThat(actualOrgUnit.orgUnitId()).isEqualTo(expectedOrgUnit.getOrgUnitId());
        assertThat(actualOrgUnit.name()).isEqualTo(expectedOrgUnit.getName());
    }

    @Test
    void shouldMapOrgUnitInfoToAccessUserOrgUnitDto() {
        List<OrgUnitInfo> orgUnitInfoList = new ArrayList<>();
        orgUnitInfoList.add(new OrgUnitInfo("aa", 1L, "orgunit", "name1"));
        orgUnitInfoList.add(new OrgUnitInfo("aa", 2L, "orgunit", "name2"));

        Page<OrgUnitInfo> orgUnits = new PageImpl<>(orgUnitInfoList);

        AccessUserOrgUnitsResponseDto result = AccessUserMapper.toAccessUserOrgUnitDto(orgUnits);

        assertThat(result.getTotalItems()).isEqualTo(orgUnits.getTotalElements());
        assertThat(result.getTotalPages()).isEqualTo(orgUnits.getTotalPages());
        assertThat(result.getCurrentPage()).isEqualTo(orgUnits.getNumber());

        List<AccessUserAccessRolesDto> accessRoles = result.getAccessRoles();
        assertThat(accessRoles).hasSize(1);

        AccessUserAccessRolesDto firstAccessRole = accessRoles.get(0);
        assertThat(firstAccessRole.getAccessRoleId()).isEqualTo("aa");
        assertThat(firstAccessRole.getOrgUnits()).hasSize(2);

        AccessUserOrgUnitDto firstOrgUnit = firstAccessRole.getOrgUnits().get(0);
        assertThat(firstOrgUnit.getScopeId()).isEqualTo(1L);
        assertThat(firstOrgUnit.getObjectType()).isEqualTo("orgunit");
        assertThat(firstOrgUnit.getName()).isEqualTo("name1");

        AccessUserOrgUnitDto secondOrgUnit = firstAccessRole.getOrgUnits().get(1);
        assertThat(secondOrgUnit.getScopeId()).isEqualTo(2L);
        assertThat(secondOrgUnit.getObjectType()).isEqualTo("orgunit");
        assertThat(secondOrgUnit.getName()).isEqualTo("name2");
    }
}


