package no.fintlabs.accessassignment;

import no.fintlabs.scope.Scope;
import no.fintlabs.scope.ScopeOrgUnit;
import no.fintlabs.scope.ScopeOrgUnitId;
import no.fintlabs.user.AccessUser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccessAssignmentMapperTest {
    @Test
    public void shouldMapToDto() {
        List<ScopeOrgUnit> scopeOrgUnits = new ArrayList<>();

        ScopeOrgUnit orgUnit1 = new ScopeOrgUnit(new ScopeOrgUnitId(1L, "orgUnit1"), new Scope());
        ScopeOrgUnit orgUnit2 = new ScopeOrgUnit(new ScopeOrgUnitId(1L, "orgUnit2"), new Scope());

        scopeOrgUnits.add(orgUnit1);
        scopeOrgUnits.add(orgUnit2);

        Scope scope = new Scope();
        scope.setId(1L);
        scope.setScopeOrgUnits(scopeOrgUnits);

        AccessAssignment accessAssignment = new AccessAssignment(new AccessAssignmentId(1L, "role1", "user1"), new AccessUser(), scope);

        AccessAssignmentDto dto = AccessAssignmentMapper.toDto(accessAssignment);

        assertNotNull(dto);
        assertEquals("role1", dto.accessRoleId());
        assertEquals(1L, dto.scopeId());
        assertEquals("user1", dto.userId());
        assertEquals(2, dto.orgUnitIds().size());
        assertEquals("orgUnit1", dto.orgUnitIds().get(0));
        assertEquals("orgUnit2", dto.orgUnitIds().get(1));
    }
}
