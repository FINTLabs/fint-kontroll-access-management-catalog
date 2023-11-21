package no.fintlabs.accessassignment.repository;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.scope.repository.Scope;
import no.fintlabs.user.repository.AccessUser;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accessassignment")
@Getter
@Setter
public class AccessAssignment {
    @EmbeddedId
    private AccessAssignmentId accessAssignmentId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private AccessUser accessUser;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("scopeId")
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("accessRoleId")
    @JoinColumn(name = "access_role_id")
    private AccessRole accessRole;

    @Override
    public String toString() {
        return "AccessAssignment{" +
               "accessAssignmentId=" + accessAssignmentId +
               ", accessUser=" + accessUser +
               ", scope=" + scope +
               ", accessRole=" + accessRole +
               '}';
    }
}
