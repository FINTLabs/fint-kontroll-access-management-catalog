package no.fintlabs.user.repository;

import jakarta.persistence.CascadeType;
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
import no.fintlabs.accessassignment.repository.AccessAssignment;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
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

    @Builder.Default
    @OneToMany(mappedBy = "accessUser", fetch = FetchType.LAZY)
    private List<AccessUserOrgUnit> accessUserOrgUnits = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "accessUser", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<AccessAssignment> accessAssignments = new ArrayList<>();

    public void addAccessAssignment(AccessAssignment accessAssignment) {
        if (accessAssignments == null) {
            accessAssignments = new ArrayList<>();
        }

        accessAssignments.add(accessAssignment);
    }

    @Override
    public String toString() {
        return "AccessUser{" +
               "resourceId='" + resourceId + '\'' +
               ", userId='" + userId + '\'' +
               ", userName='" + userName + '\'' +
               ", userType='" + userType + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", mainOrganisationUnitName='" + mainOrganisationUnitName + '\'' +
               ", mainOrganisationUnitId='" + mainOrganisationUnitId + '\'' +
               ", mobilePhone='" + mobilePhone + '\'' +
               ", email='" + email + '\'' +
               ", managerRef='" + managerRef + '\'' +
               '}';
    }
}
