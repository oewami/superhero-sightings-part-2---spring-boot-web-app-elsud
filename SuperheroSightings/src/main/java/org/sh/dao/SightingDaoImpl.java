package org.sh.dao;

import org.sh.dto.Location;
import org.sh.dto.Sighting;

import org.sh.dto.Superhero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Repository
@Profile("prod")
public class SightingDaoImpl implements SightingDao {

    @Autowired
    JdbcTemplate jdbcTemplate;


    //review select statements
    @Override
    public Sighting getSighting(int sightingId){
        final String SELECT_SIGHTING = "SELECT * FROM sighting "
                + "INNER JOIN superhero ON sighting.superheroId = superhero.id "
                + "INNER JOIN location ON sighting.locationId = location.id "
                + "WHERE sighting.id = ?;";
        try {
            return jdbcTemplate.queryForObject(
                    SELECT_SIGHTING, new SightingDaoImpl.SightingMapper(), sightingId
            );
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<Sighting> listSightings(){
        final String SELECT_SIGHTINGS = "SELECT * FROM sighting "
                + "INNER JOIN superhero ON sighting.superheroId = superhero.id "
                + "INNER JOIN location ON sighting.locationId = location.id;";
        return jdbcTemplate.query(SELECT_SIGHTINGS, new SightingDaoImpl.SightingMapper());
    }

    @Override
    public boolean editSighting(Sighting sighting) throws NotUniqueException {

        final String UPDATE_SIGHTING = "UPDATE sighting SET superheroId = ?, date = ?, locationId = ? "
                + "where id =  ?;";
        try {
            return jdbcTemplate.update(
                    UPDATE_SIGHTING, sighting.getSuperhero().getId(),
                    Timestamp.valueOf(sighting.getDate().atStartOfDay()),
                    sighting.getLocation().getId(), sighting.getId()) > 0;
        } catch (DataAccessException ex) {
            throw new NotUniqueException("Sighting with these data already exists");
        }
    }

    @Override
    public boolean deleteSighting(int sightingId){
        final String DELETE_SIGHTING = "DELETE FROM sighting WHERE id = ?;";
        return jdbcTemplate.update(DELETE_SIGHTING, sightingId) > 0;
    }

    @Override
    public Sighting addSighting(Sighting sighting) throws NotUniqueException {
        GeneratedKeyHolder keyHolder = new  GeneratedKeyHolder();
        final String INSERT_SIGHTING = "INSERT INTO sighting "
                + "(locationId, superheroId, date) VALUES(?, ?, ?);";
        try {
            jdbcTemplate.update((Connection conn) -> {
                PreparedStatement statement = conn.prepareStatement(
                        INSERT_SIGHTING, Statement.RETURN_GENERATED_KEYS
                );
                statement.setInt(1, sighting.getLocation().getId());
                statement.setInt(2, sighting.getSuperhero().getId());
                statement.setTimestamp(3, Timestamp.valueOf(sighting.getDate().atStartOfDay()));
                return statement;
            }, keyHolder);
        } catch (DataAccessException ex) {
            throw new NotUniqueException("Sighting name should be unique");
        }
        sighting.setId(keyHolder.getKey().intValue());
        return sighting;
    }

    @Override
    public List<Sighting> listSightings(LocalDate date){
        return jdbcTemplate.query("select * from sighting where date = ?;",
                new SightingMapper(), date);
    }

    @Override
    public List<Sighting> listLastSightings(){
        return jdbcTemplate.query("select * from sighting order by date desc limit 10",
                new SightingMapper());
    }

    private final class SightingMapper implements RowMapper<Sighting> {

        @Override
        public Sighting mapRow(ResultSet resultSet, int i) throws SQLException {
            Location location = new Location();
            Sighting sighting = new Sighting();
            Superhero superhero = new Superhero();

            location.setId(resultSet.getInt("location.id"));
            location.setName(resultSet.getString("location.name"));
            location.setDescription(resultSet.getString("location.description"));
            location.setAddress(resultSet.getString("location.address"));
            location.setLatitude(resultSet.getBigDecimal("location.latitude"));
            location.setLongitude(resultSet.getBigDecimal("location.longitude"));

            superhero.setId(resultSet.getInt("superhero.id"));
            superhero.setName(resultSet.getString("superhero.name"));
            superhero.setDescription(resultSet.getString("superhero.description"));

            sighting.setDate(resultSet.getTimestamp("sighting.date").toLocalDateTime().toLocalDate());
            sighting.setLocation(location);
            sighting.setSuperhero(superhero);
            sighting.setId(resultSet.getInt("sighting.id"));
            return sighting;
        }
    }

}
