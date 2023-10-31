package no.fintlabs.user;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    private String userType;
    private String firstName;
    private String lastName;
    private String mainOrganisationUnitName;
    private String mainOrganisationUnitId;
    private String mobilePhone;
    private String email;
    private String managerRef;

    @ElementCollection
    @CollectionTable(name = "accessuser_orgunit_ids", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "organisation_unit_ids")
    private List<String> organisationUnitIds;

    @OneToMany(mappedBy = "accessUser", fetch = FetchType.EAGER)
    private List<AccessAssignment> accessAssignments;

    public void addAccessAssignment(AccessAssignment accessAssignment) {
        if (accessAssignments == null) {
            accessAssignments = new ArrayList<>();
        }

        accessAssignments.add(accessAssignment);
    }
}
