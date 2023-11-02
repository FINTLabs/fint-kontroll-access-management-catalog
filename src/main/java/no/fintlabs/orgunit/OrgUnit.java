package no.fintlabs.orgunit;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import lombok.Setter;
import lombok.ToString;
import no.fintlabs.scope.ScopeOrgUnit;
import no.fintlabs.user.AccessUserOrgUnit;

import java.util.List;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "org_unit")
@Getter
@Setter
public class OrgUnit {
    @Id
    @Column(name = "org_unit_id", unique = true, nullable = false)
    @JsonProperty("organisationUnitId")
    private String orgUnitId;

    private String name;
    private String shortName;

    @OneToMany(mappedBy = "orgUnit", fetch = FetchType.EAGER)
    private List<ScopeOrgUnit> scopeOrgUnits;

    @OneToMany(mappedBy = "orgUnit", fetch = FetchType.EAGER)
    private List<AccessUserOrgUnit> accessUserOrgUnits;
}
