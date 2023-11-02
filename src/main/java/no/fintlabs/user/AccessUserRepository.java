package no.fintlabs.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessUserRepository extends JpaRepository<AccessUser, String>, JpaSpecificationExecutor<AccessUser> {
    Page<AccessUser> findAll(Pageable pageable);
}
