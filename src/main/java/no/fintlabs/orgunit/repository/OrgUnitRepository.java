package no.fintlabs.orgunit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrgUnitRepository extends JpaRepository<OrgUnit, String> {

    Optional<OrgUnit> findByOrgUnitId(String orgUnitId);

    @Query("SELECT new no.fintlabs.orgunit.repository.OrgUnitInfo(aa.accessRole.accessRoleId, s.id, s.objectType, ou.name) " +
           "FROM AccessUser au " +
           "JOIN au.accessAssignments aa " +
           "JOIN aa.scopes s " +
           "JOIN s.orgUnits ou " +
           "WHERE au.resourceId = :resourceId " +
           "AND (:accessRoleId IS NULL OR aa.accessRole.accessRoleId = :accessRoleId) " +
           "AND (:objectType IS NULL OR s.objectType = :objectType) " +
           "AND (:orgUnitName IS NULL OR LOWER(ou.name) LIKE LOWER(CONCAT('%', :orgUnitName, '%')))")
    Page<OrgUnitInfo> findOrgUnitsForUserByFilters(String resourceId, String accessRoleId, String objectType, String orgUnitName,
                                                   Pageable pageable);

}
