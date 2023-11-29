package no.fintlabs.accessassignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessAssignmentRepository extends JpaRepository<AccessAssignment, Long> {

    List<AccessAssignment> findByAccessUserResourceId(String resourceId);
}
