package com.oreilly.persistence.dao;

import com.oreilly.persistence.entities.Officer;
import com.oreilly.persistence.entities.Rank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Makes the class spring managed. You can only Autowire into a bean spring is managing.
// It also does exception translation for SQL exceptions to spring exception hierarchy.
@Repository
@Component
public class jdbcOfficerDAO implements OfficerDAO {

    // We will auto-wire JdbcTemplate with the constructor.
    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insertOfficer;

    private final RowMapper<Officer> officerMapper = (rs, rowNum) ->
        new Officer(rs.getInt("id"),
            Rank.valueOf(rs.getString("rank")),
            rs.getString("first_name"),
            rs.getString("last_name"));

    // Autowiring the JdbcTemplate with a constructor argument.
    @Autowired // <optional>
    public jdbcOfficerDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        // Instantiating a SimpleJdbcInsert from a jdbcTemplate, this is used in the save method.
        insertOfficer = new SimpleJdbcInsert(jdbcTemplate)
            .withTableName("officers")
            .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<Officer> findById(Integer id) {
        if(!existsById(id)) return Optional.empty();
        return Optional.of(jdbcTemplate.queryForObject(
            "SELECT * FROM officers WHERE id=?", officerMapper, id));
    }

    @Override
    public List<Officer> findAll() {
        return jdbcTemplate.query("SELECT * FROM officers", officerMapper);
    }

    @Override
    public Officer save(Officer officer) {
        Map<String, Object> parameters = new HashMap<>();
        // Mapping column names and values for insertOfficer.
        parameters.put("rank", officer.getRank());
        parameters.put("first_name", officer.getFirstName());
        parameters.put("last_name", officer.getLastName());

        // Utilizing the SimpleJdbcInsert method executeAndReturnKey, the argument is a map of column names
        // to values.
        Integer newId = (Integer) insertOfficer.executeAndReturnKey(parameters);
        officer.setId(newId);

        return officer;
    }

    // Return a long representing how many officers are in the database.
    @Override
    public long count() {
        // Second argument defines the return type (long).
        return jdbcTemplate.queryForObject("select count(*) from officers", Long.class);
    }

    @Override
    public void delete(Officer officer) {
        // The update method here is used for inserts, updates and deletes.
        // Second parameter comes from the argument.
        jdbcTemplate.update("DELETE FROM officers WHERE id=?", officer.getId());
    }

    @Override
    public boolean existsById(Integer id) {
        return jdbcTemplate.queryForObject(
            "SELECT EXISTS(SELECT 1 FROM officers WHERE id=?)", Boolean.class, id);
    }

}
