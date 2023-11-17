package no.fintlabs.orgunit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrgUnitRepository extends JpaRepository<OrgUnit, String> {

    Optional<OrgUnit> findByOrgUnitId(String orgUnitId);
}