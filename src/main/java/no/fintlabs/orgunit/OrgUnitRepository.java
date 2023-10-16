package no.fintlabs.orgunit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgUnitRepository extends JpaRepository<OrgUnit, String> {
}
