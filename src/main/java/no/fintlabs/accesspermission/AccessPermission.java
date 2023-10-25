package no.fintlabs.accesspermission;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.feature.Feature;

@Builder
@Entity
@ToString
@Table(name = "accesspermission", uniqueConstraints = { @UniqueConstraint(columnNames = { "accessRoleId", "featureId", "operation" }) })
@Getter
@Setter
public class AccessPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accessRoleId;

    private Long featureId;

    private String operation;

    @ManyToOne
    @JoinColumn(name = "accessRoleId", insertable = false, updatable = false)
    private AccessRole accessRole;

    @ManyToOne
    @JoinColumn(name = "featureId", insertable = false, updatable = false)
    private Feature feature;

    public AccessPermission() {
    }

    public AccessPermission(String accessRoleId, Long featureId, String operation) {
        this.accessRoleId = accessRoleId;
        this.featureId = featureId;
        this.operation = operation;
    }

    public AccessPermission(Long id, String accessRoleId, Long featureId, String operation, AccessRole accessRole, Feature feature) {
        this.id = id;
        this.accessRoleId = accessRoleId;
        this.featureId = featureId;
        this.operation = operation;
        this.accessRole = accessRole;
        this.feature = feature;
    }
}
