package no.fintlabs.accessassignment;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.user.AccessUser;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class AccessAssignmentId implements Serializable {
    private Long scopeId;
    private String accessRoleId;
    private String userId;
}
