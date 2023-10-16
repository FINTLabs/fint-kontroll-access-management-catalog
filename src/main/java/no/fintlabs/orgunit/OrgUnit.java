package no.fintlabs.orgunit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "org_unit")
@Getter
public class OrgUnit {

    private Long id;

    @Id
    @Column(name = "org_unit_id")
    private String orgUnitId;
}
