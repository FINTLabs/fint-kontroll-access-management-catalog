package no.fintlabs.scope;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Getter
@EqualsAndHashCode
public class ScopeOrgUnitId implements Serializable {
    private Long scopeId;
    private Long orgUnitId;
}
