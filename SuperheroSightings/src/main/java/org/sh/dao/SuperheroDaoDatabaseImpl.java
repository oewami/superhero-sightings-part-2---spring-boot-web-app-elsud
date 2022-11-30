package org.sh.dao;

import org.sh.dto.Superhero;

import org.sh.dto.Superpower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.List;

@Repository
@Profile("prod")
public class SuperheroDaoDatabaseImpl implements SuperheroDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Superhero getSuperhero(int id) {
        final String SELECT_HERO = "SELECT * FROM superhero INNER JOIN superpower "
                + "ON superhero.superpower = superpower.id WHERE superhero.id=?;";
        try {
            return jdbcTemplate.queryForObject(
                    SELECT_HERO, new SuperheroMapper(), id
            );
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<Superhero> listSuperhero() {
        final String SELECT_HEROES = "SELECT * FROM superhero INNER JOIN "
                + "superpower ON superhero.superpower = superpower.id;";
        return jdbcTemplate.query(SELECT_HEROES, new SuperheroMapper());
    }

    @Override
    public boolean editSuperhero(Superhero superhero) throws NotUniqueException {
        final String UPDATE_HERO = "UPDATE superhero SET name = ?, description = ?, "
                + "superpower = ? WHERE id = ?;";
        try {
            return jdbcTemplate.update(
                    UPDATE_HERO, superhero.getName(), superhero.getDescription(),
                    superhero.getSuperpower().getId(), superhero.getId()) > 0;
        } catch (DataAccessException ex) {
            throw new NotUniqueException("Superhero name should be unique");
        }
    }

    @Override
    @Transactional
    public boolean deleteSuperhero(int superheroId) {
        // catch possible exception here or in controller!
        final String DELETE_ORGANIZATION_CONN = "DELETE FROM superheroOrganization WHERE superheroId = ?;";
        jdbcTemplate.update(DELETE_ORGANIZATION_CONN, superheroId);
        final String DELETE_SIGHTING = "DELETE FROM sighting WHERE superheroId = ?;";
        jdbcTemplate.update(DELETE_SIGHTING, superheroId);
        final String DELETE_HERO = "DELETE FROM superhero WHERE id = ?;";
        return jdbcTemplate.update(DELETE_HERO, superheroId) > 0;
    }

    @Override
    public Superhero addSuperhero(Superhero superhero) throws NotUniqueException {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        final String INSERT_HERO = "INSERT INTO superhero "
                + "(name, description, superpower) VALUES(?, ?, ?);";
        try {
            jdbcTemplate.update((Connection conn) -> {
                PreparedStatement statement = conn.prepareStatement(
                        INSERT_HERO, Statement.RETURN_GENERATED_KEYS
                );
                statement.setString(1, superhero.getName());
                statement.setString(2, superhero.getDescription());
                statement.setInt(3, superhero.getSuperpower().getId());
                return statement;
            }, keyHolder);
        } catch (DataAccessException ex) {
            throw new NotUniqueException("Superhero name should be unique");
        }
        superhero.setId(keyHolder.getKey().intValue());
        return superhero;
    }

    @Override
    public List<Superhero> listSuperheroForLocation(int locationId) {
        // maybe should move it inside SightingDao
        final String SELECT_NOTICED_HEROES = "SELECT DISTINCT superhero.id, superhero.name, "
                + "superhero.description, superpower.id, superpower.name FROM superhero "
                + "INNER JOIN superpower ON superhero.superpower = superpower.id "
                + "INNER JOIN sighting ON superhero.id = sighting.superheroId "
                + "WHERE sighting.locationId = ?;";
        return jdbcTemplate.query(SELECT_NOTICED_HEROES, new SuperheroMapper(), locationId);
    }

    @Override
    public List<Superhero> listSuperheroForOrganization(int organizationId) {
        final String SELECT_MEMBERS = "SELECT superhero.id, superhero.name, "
                + "superhero.description, superpower.id, superpower.name "
                + "FROM superhero INNER JOIN superpower ON superhero.superpower = superpower.id "
                + "INNER JOIN superheroOrganization so ON so.superheroId = superhero.id "
                + "WHERE so.organizationId = ?;";
        return jdbcTemplate.query(SELECT_MEMBERS, new SuperheroMapper(), organizationId);
    }

    public static class SuperheroMapper implements RowMapper<Superhero> {

        @Override
        public Superhero mapRow(ResultSet resultSet, int i) throws SQLException {
            Superhero superhero = new Superhero();
            Superpower superpower = new Superpower();
            superhero.setId(resultSet.getInt("superhero.id"));
            superhero.setName(resultSet.getString("superhero.name"));
            superhero.setDescription(resultSet.getString("superhero.description"));
            superpower.setId(resultSet.getInt("superpower.id"));
            superpower.setName(resultSet.getString("superpower.name"));
            superhero.setSuperpower(superpower);
            return superhero;
        }
    }

    private final class SuperheroShortMapper implements RowMapper<Superhero> {

        @Override
        public Superhero mapRow(ResultSet resultSet, int i) throws SQLException {
            Superhero superhero = new Superhero();
            superhero.setId(resultSet.getInt("id"));
            superhero.setName(resultSet.getString("name"));
            superhero.setDescription(resultSet.getString("description"));
            return superhero;
        }
    }
}
