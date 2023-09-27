package no.fintlabs;

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

    @Test
    public void test() {
        List<Object> objects = jdbcTemplate.query("select id, accessrole_id, name from accessrole", new BeanPropertyRowMapper<>(Object.class));
        assertNotNull(objects);
        assertThat(objects).hasSize(4);
    }
}
