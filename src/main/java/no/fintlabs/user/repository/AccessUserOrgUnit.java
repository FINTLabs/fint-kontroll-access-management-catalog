package no.fintlabs.user.repository;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.fintlabs.orgunit.repository.OrgUnit;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "access_user_org_unit")
@Getter
@Setter
public class AccessUserOrgUnit {
    @EmbeddedId
    private AccessUserOrgUnitId accessUserOrgUnitId;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private AccessUser accessUser;

    @ManyToOne
    @MapsId("orgUnitId")
    @JoinColumn(name = "org_unit_id")
    private OrgUnit orgUnit;
}
