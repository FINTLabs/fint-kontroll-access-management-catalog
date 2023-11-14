package no.fintlabs.feature.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import no.fintlabs.accesspermission.repository.AccessPermission;

import java.util.List;

@Builder
@Entity
@ToString
@Table(name = "feature")
@Getter
@Setter
public class Feature {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String path;

    @OneToMany(mappedBy = "feature")
    private List<AccessPermission> accessPermissions;

    public Feature() {
    }

    public Feature(Long id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public Feature(Long id, String name, String path, List<AccessPermission> accessPermissions) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.accessPermissions = accessPermissions;
    }
}
