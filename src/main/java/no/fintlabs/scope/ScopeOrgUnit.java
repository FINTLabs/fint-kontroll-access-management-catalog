package no.fintlabs.scope;

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
import no.fintlabs.orgunit.OrgUnit;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "scope_orgunit")
@Getter
@Setter
public class ScopeOrgUnit {
    @EmbeddedId
    private ScopeOrgUnitId scopeOrgUnitId;

    @ManyToOne(optional = false)
    @MapsId("scopeId")
    @JoinColumn(name = "scope_id", nullable = false)
    private Scope scope;

    @ManyToOne(optional = false)
    @MapsId("orgUnitId")
    @JoinColumn(name = "org_unit_id", nullable = false)
    private OrgUnit orgUnit;
}
