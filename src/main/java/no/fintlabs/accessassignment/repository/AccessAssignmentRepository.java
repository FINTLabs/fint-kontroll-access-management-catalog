package no.fintlabs.accessassignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessAssignmentRepository extends JpaRepository<AccessAssignment, AccessAssignmentId> {
}
