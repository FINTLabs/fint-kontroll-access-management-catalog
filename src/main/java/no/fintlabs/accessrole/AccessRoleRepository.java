package no.fintlabs.accessrole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessRoleRepository extends JpaRepository<AccessRole, String> {

}
