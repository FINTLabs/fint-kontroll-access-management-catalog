package no.fintlabs.orgunit.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "org_unit")
@Getter
@Setter
@ToString
public class OrgUnit {
    @Id
    @Column(name = "org_unit_id", unique = true, nullable = false)
    @JsonProperty("organisationUnitId")
    private String orgUnitId;

    private String name;
    private String shortName;

}
