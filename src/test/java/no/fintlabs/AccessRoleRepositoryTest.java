package no.fintlabs;

import no.fintlabs.accessrole.AccessRole;
import no.fintlabs.accessrole.AccessRoleRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class AccessRoleRepositoryTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AccessRoleRepository accessRoleRepository;

    @Disabled
    public void test() {
        List<Object> objects = jdbcTemplate.query("select id, access_role_id, name from accessrole", new BeanPropertyRowMapper<>(Object.class));
        assertNotNull(objects);
        assertThat(objects).hasSize(4);
    }

    @Disabled
    public void shouldGetAccessRoles() {
        List<AccessRole> all = accessRoleRepository.findAll();
        assertThat(all).hasSize(4);
        assertThat(all.get(0).getAccessRoleId()).isEqualTo("ata");
        assertThat(all.get(0).getName()).isEqualTo("applikasjonstilgangsadministrator");
    }
}
