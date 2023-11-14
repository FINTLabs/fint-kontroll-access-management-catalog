package no.fintlabs.feature.repository;

import no.fintlabs.feature.AccessRoleFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {

    @Query("SELECT new no.fintlabs.feature.AccessRoleFeature(f.name, f.path, ap.operation) " +
           "FROM Feature f JOIN f.accessPermissions ap " +
           "WHERE ap.accessRole.accessRoleId = :accessRoleId")
    List<AccessRoleFeature> findFeaturesByAccessRoleId(@Param("accessRoleId") String accessRoleId);
}
