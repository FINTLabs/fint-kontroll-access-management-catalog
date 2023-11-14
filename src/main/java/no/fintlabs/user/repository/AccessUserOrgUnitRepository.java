package no.fintlabs.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessUserOrgUnitRepository extends JpaRepository<AccessUserOrgUnit, AccessUserOrgUnit> {
}
