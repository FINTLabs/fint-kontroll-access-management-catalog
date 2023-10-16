package no.fintlabs.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessUserRepository extends JpaRepository<AccessUser, String> {

    @Query("SELECT new no.fintlabs.user.AccessUserScopeDto(u.userId, aa.accessAssignmentId.accessRoleId, s.objectType, sou.scopeOrgUnitId.orgUnitId) " +
           "FROM AccessUser u " +
           "JOIN u.accessAssignments aa " +
           "JOIN aa.scope s " +
           "LEFT JOIN s.scopeOrgUnits sou")
    List<AccessUserScopeDto> fetchUserAccess();
}
