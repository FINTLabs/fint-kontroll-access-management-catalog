package no.fintlabs.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessUserRepository extends JpaRepository<AccessUser, String> {
}
