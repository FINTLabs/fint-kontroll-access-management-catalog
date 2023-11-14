package no.fintlabs.user.repository;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class AccessUserOrgUnitId implements Serializable {
    private String userId;
    private String orgUnitId;
}
