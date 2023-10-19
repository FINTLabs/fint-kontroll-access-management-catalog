package no.fintlabs.user;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.fintlabs.accessassignment.AccessAssignment;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "accessuser")
@Getter
@Setter
public class AccessUser {
    @Id
    private String resourceId;

    private String userId;

    private String userName;

    private String firstName;

    private String lastName;

    @OneToMany(mappedBy = "accessUser", fetch = FetchType.LAZY)
    private List<AccessAssignment> accessAssignments;

    public void addAccessAssignment(AccessAssignment accessAssignment) {
        if(accessAssignments == null) {
            accessAssignments = new ArrayList<>();
        }

        accessAssignments.add(accessAssignment);
    }
}
