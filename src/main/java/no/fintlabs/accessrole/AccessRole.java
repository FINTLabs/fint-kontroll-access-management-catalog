package no.fintlabs.accessrole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import no.fintlabs.accesspermission.AccessPermission;

import java.util.List;

@Getter
@Builder
@Entity
@Table(name = "accessrole")
public class AccessRole {
    @Id()
    @Column(name = "access_role_id")
    private String accessRoleId;

    private String name;

    @OneToMany(mappedBy = "accessRole")
    private List<AccessPermission> accessPermissions;

    public AccessRole() {
    }

    public AccessRole(String accessRoleId, String name) {
        this.accessRoleId = accessRoleId;
        this.name = name;
    }

    public AccessRole(String accessRoleId, String name, List<AccessPermission> accessPermissions) {
        this.accessRoleId = accessRoleId;
        this.name = name;
        this.accessPermissions = accessPermissions;
    }
}
