package no.fintlabs.accesspermission.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessPermissionRepository extends JpaRepository<AccessPermission, Long> {

    List<AccessPermission> findByAccessRoleId(String accessRoleId);
}
