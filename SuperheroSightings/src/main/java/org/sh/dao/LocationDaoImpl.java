package org.sh.dao;

import org.sh.dto.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Profile("prod")
public class LocationDaoImpl implements LocationDao {

    /**
     * 	id INT AUTO_INCREMENT PRIMARY KEY,
     * 	name VARCHAR(40) NOT NULL,
     *     description VARCHAR(160),
     *     address VARCHAR(80) NOT NULL,
     *     longitude VARCHAR(10) NOT NULL,
     *     latitude VARCHAR(10) NOT NULL,
     *     CONSTRAINT UNIQUE (longitude, latitude)
     */
    private Map<Integer, Location> locations = new HashMap<>();

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     *
     * @param locationId
     * @return
     */
    @Override
    public Location getLocation(int locationId) {
        final String SELECT_LOCATION = "SELECT * FROM location WHERE id = ?;";
        try {
            return jdbcTemplate.queryForObject(SELECT_LOCATION, new LocationMapper(), locationId);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Location> listLocations() {
        final String SELECT_LOCATIONS = "SELECT * FROM location;";
        return jdbcTemplate.query(SELECT_LOCATIONS, new LocationMapper());
    }

    @Override
    public boolean editLocation(Location location) {
        final String UPDATE_LOCATION = "UPDATE location SET name = ? WHERE id = ?;";
        try {
            return jdbcTemplate.update(UPDATE_LOCATION, location.getName(), location.getId()) > 0;
        } catch (DataAccessException e) {
            System.out.println("Invalid location");
        }
        return false;
    }

    @Override
    public boolean deleteLocation(int locationId) throws DeletionException {
        final String DELETE_LOCATION = "DELETE FROM location WHERE id = ?;";
        try {
            return jdbcTemplate.update(DELETE_LOCATION, locationId) > 0;
        } catch (DataIntegrityViolationException e) {
            throw new DeletionException("Cannot delete location if it is associated with a superhero");
        }
    }

    @Override
    public Location addLocation(Location location) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        final String INSERT_LOCATION = "INSERT INTO location (name, description, address, longitude, latitude) VALUES (?, ?, ?, ?, ?);";
        try {
            jdbcTemplate.update((Connection conn) -> {
                PreparedStatement statement = conn.prepareStatement(
                        INSERT_LOCATION, Statement.RETURN_GENERATED_KEYS
                );
                statement.setString(1, location.getName());
                statement.setString(2, location.getDescription());
                statement.setString(3, location.getAddress());
                statement.setBigDecimal(4, location.getLongitude());
                statement.setBigDecimal(5, location.getLatitude());
                return statement;
            }, keyHolder);
        } catch (DataAccessException e) {
            System.out.println("COULD not create new location");
        }
        location.setId(keyHolder.getKey().intValue());
        return location;
    }

    public List<Location> listSuperheroLocations(int superheroId) {
        final String SELECT_SUPERHERO_LOCATIONS = "SELECT * FROM sightings WHERE superheroId = ?;";
        try {
            List<Location> superlocations = jdbcTemplate.query(SELECT_SUPERHERO_LOCATIONS, new LocationMapper(), superheroId);
            for(Location location : superlocations) {
                System.out.println(location.getName());
            }
            return superlocations;
        } catch (DataAccessException e) {
            return null;
        }
    }

    public static class LocationMapper implements RowMapper<Location> {

        @Override
        public Location mapRow(ResultSet resultSet, int i) throws SQLException {
            Location location = new Location();
            location.setId(resultSet.getInt("id"));
            location.setName(resultSet.getString("name"));
            location.setDescription(resultSet.getString("description"));
            location.setAddress(resultSet.getString("address"));
            location.setLatitude(resultSet.getBigDecimal("latitude"));
            location.setLongitude(resultSet.getBigDecimal("longitude"));
            return location;
        }
    }
}
