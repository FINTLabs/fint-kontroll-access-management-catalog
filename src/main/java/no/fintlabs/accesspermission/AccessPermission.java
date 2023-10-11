package no.fintlabs.accesspermission;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.feature.Feature;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "accesspermission")
@Getter
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

}
