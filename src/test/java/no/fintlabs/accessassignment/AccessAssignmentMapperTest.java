package no.fintlabs.accessassignment;

import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.orgunit.OrgUnit;
import no.fintlabs.scope.Scope;
import no.fintlabs.scope.ScopeOrgUnit;
import no.fintlabs.scope.ScopeOrgUnitId;
import no.fintlabs.user.repository.AccessUser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccessAssignmentMapperTest {
    @Test
    public void shouldMapToDto() {
        List<ScopeOrgUnit> scopeOrgUnits = new ArrayList<>();

        OrgUnit orgUnit1 = new OrgUnit("198", "orgUnit1", "shortname1", scopeOrgUnits, null);
        OrgUnit orgUnit2 = new OrgUnit("153", "orgUnit2", "shortname2", scopeOrgUnits, null);

        ScopeOrgUnit scopeOrgUnit1 = new ScopeOrgUnit(new ScopeOrgUnitId(1L, "198"), new Scope(), orgUnit1);
        ScopeOrgUnit scopeOrgUnit2 = new ScopeOrgUnit(new ScopeOrgUnitId(1L, "153"), new Scope(), orgUnit2);

        scopeOrgUnits.add(scopeOrgUnit1);
        scopeOrgUnits.add(scopeOrgUnit2);

        Scope scope = new Scope();
        scope.setId(1L);
        scope.setScopeOrgUnits(scopeOrgUnits);

        AccessAssignment accessAssignment = new AccessAssignment(new AccessAssignmentId(1L, "role1", "user1"), new AccessUser(), scope, new AccessRole());

        AccessAssignmentDto dto = AccessAssignmentMapper.toDto(accessAssignment);

        assertNotNull(dto);
        assertEquals("role1", dto.accessRoleId());
        assertEquals(1L, dto.scopeId());
        assertEquals("user1", dto.userId());
        assertEquals(2, dto.orgUnitIds().size());
        assertEquals("198", dto.orgUnitIds().get(0));
        assertEquals("153", dto.orgUnitIds().get(1));
    }
}
