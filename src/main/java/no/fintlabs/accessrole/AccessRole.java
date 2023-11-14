package no.fintlabs.accessrole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.fintlabs.accessassignment.AccessAssignment;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "accessrole")
public class AccessRole {
    @Id()
    @Column(name = "access_role_id")
    private String accessRoleId;

    private String name;

    @OneToMany(mappedBy = "accessRole", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AccessAssignment> accessAssignments;
}
