package no.fintlabs.accessassignment.repository;

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
public class AccessAssignmentId implements Serializable {
    private Long scopeId;
    private String accessRoleId;
    private String userId;
}
