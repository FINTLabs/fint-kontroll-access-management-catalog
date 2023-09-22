package no.fintlabs;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class RoleRepositoryTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void test() {
        String testcol = jdbcTemplate.queryForObject("select testcol from test_table_accessmanagement", String.class);
        assertNotNull(testcol);
    }
}
