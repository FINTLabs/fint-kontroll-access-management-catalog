package no.fintlabs.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AccessUserOrgUnitsResponseDto {

    private long totalItems;
    private int totalPages;
    private int currentPage;
    private List<AccessUserAccessRolesDto> accessRoles;
}
