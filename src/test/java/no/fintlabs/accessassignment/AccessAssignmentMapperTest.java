package no.fintlabs.accessassignment;

import no.fintlabs.accessassignment.repository.AccessAssignment;
import no.fintlabs.user.AccessAssignmentMother;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccessAssignmentMapperTest {
    @Test
    public void shouldMapToDto() {
        AccessAssignment accessAssignment = AccessAssignmentMother.createDefaultAccessAssignment();

        AccessAssignmentDto dto = AccessAssignmentMapper.toDto(accessAssignment);

        assertNotNull(dto);
        assertEquals(999L, dto.id());
        assertEquals("ata", dto.accessRoleId());
        assertEquals("123", dto.userId());
        assertEquals(1L, dto.scopes().get(0).id());
        assertEquals(2, dto.scopes().get(0).orgUnits().size());
        assertEquals("198", dto.scopes().get(0).orgUnits().get(0).orgUnitId());
        assertEquals("153", dto.scopes().get(0).orgUnits().get(1).orgUnitId());
    }

    @Test
    public void shouldMapListToDto() {
        List<AccessAssignment> accessAssignment = List.of(AccessAssignmentMother.createDefaultAccessAssignment());

        List<AccessAssignmentDto> dtos = AccessAssignmentMapper.toDto(accessAssignment);

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        AccessAssignmentDto dto = dtos.get(0);
        assertEquals(999L, dto.id());
        assertEquals("ata", dto.accessRoleId());
        assertEquals("123", dto.userId());
        assertEquals(1L, dto.scopes().get(0).id());
        assertEquals(2, dto.scopes().get(0).orgUnits().size());
        assertEquals("198", dto.scopes().get(0).orgUnits().get(0).orgUnitId());
        assertEquals("153", dto.scopes().get(0).orgUnits().get(1).orgUnitId());
    }
}
