package no.fintlabs.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.fintlabs.orgunit.OrgUnit;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter@Entity
@Table(name = "accessuser_orgunit_ids")
public class AccessUserOrganisationId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Assuming you have an auto-generated primary key.
    private Long id;

    @Column(name = "organisation_unit_ids", insertable = false, updatable = false)
    private String organisationUnitId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "resourceId")
    private AccessUser accessUser;

    @ManyToOne
    @JoinColumn(name = "organisation_unit_ids", referencedColumnName = "org_unit_id")
    private OrgUnit orgUnit;
}
