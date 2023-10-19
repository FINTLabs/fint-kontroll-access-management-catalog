package no.fintlabs.orgunit;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import no.fintlabs.scope.ScopeOrgUnit;

import java.util.List;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "org_unit")
@Getter
public class OrgUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "org_unit_id", unique = true, nullable = false)
    @JsonProperty("organisationUnitId")
    private String orgUnitId;

    @OneToMany(mappedBy = "orgUnit", fetch = FetchType.LAZY)
    private List<ScopeOrgUnit> scopeOrgUnits;
}
