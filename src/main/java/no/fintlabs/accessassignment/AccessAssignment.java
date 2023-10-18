package no.fintlabs.accessassignment;

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
import lombok.ToString;
import no.fintlabs.scope.Scope;
import no.fintlabs.user.AccessUser;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "accessassignment")
@Getter
public class AccessAssignment {
    @EmbeddedId
    private AccessAssignmentId accessAssignmentId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private AccessUser accessUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("scopeId")
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;
}
