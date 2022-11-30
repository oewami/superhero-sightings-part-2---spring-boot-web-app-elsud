package org.sh.dao;

import org.sh.dto.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Profile("prod")
public class OrganizationDaoImpl implements OrganizationDao {
    @Autowired
    JdbcTemplate jdbcTemplate;


    @Override
    public Organization getOrganization(int id) {
        final String SELECT_ORGANIZATION = "SELECT * FROM organization where id = ?;";
        try {
            Organization organization = jdbcTemplate.queryForObject(
                    SELECT_ORGANIZATION, new OrganizationDaoImpl.OrganizationMapper(), id
            );
            organization.setMembers(getSuperheros(id));
            return organization;
        } catch (DataAccessException ex) {
            return null;
        }
    }


    @Override
    public List<Organization> listOrganizations() {
        final String SELECT_ORGANIZATIONS = "SELECT * FROM organization;";
        List<Organization> organizations =  jdbcTemplate.query(
                SELECT_ORGANIZATIONS, new OrganizationDaoImpl.OrganizationMapper());
        organizations.stream()
                .forEach(org -> org.setMembers(getSuperheros(org.getId())));
        return organizations;
    }


    @Transactional
    @Override
    public boolean editOrganization(Organization organization) {
        final String UPDATE_ORGANIZATION = "UPDATE organization SET name = ?, description = ?, "
                + "address = ?  WHERE id = ?;";
        final String DELETE_SUPERHERO_ORGANIZATION = "DELETE FROM superheroOrganization "
                + "WHERE organizationId = ?;";
        final String INSERT_SUPERHERO_ORGANIZATION = "INSERT INTO superheroOrganization "
                + "(organizationId, superheroId) VALUES (?,?);";
        jdbcTemplate.update(DELETE_SUPERHERO_ORGANIZATION, organization.getId());
        for (Superhero superhero : organization.getMembers()) {
            jdbcTemplate.update(
                    INSERT_SUPERHERO_ORGANIZATION,
                    organization.getId(),
                    superhero.getId());
        }
        return jdbcTemplate.update(
                UPDATE_ORGANIZATION, organization.getName(), organization.getDescription(),
                organization.getAddress(), organization.getId()) > 0;
    }


    //review
    @Transactional
    @Override
    public boolean deleteOrganization(int organizationId) {
        final String DELETE_SUPERHERO_CONN = "DELETE FROM superheroOrganization WHERE organizationId = ?;";
        jdbcTemplate.update(DELETE_SUPERHERO_CONN, organizationId);
        final String DELETE_ORGANIZATION= "DELETE FROM organization WHERE id = ?;";
        return jdbcTemplate.update(DELETE_ORGANIZATION, organizationId) > 0;
    }


    @Transactional
    @Override
    public Organization addOrganization(Organization organization) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        final String INSERT_ORGANIZATION = "INSERT INTO organization "
                + "(name, description, address) VALUES(?, ?, ?);";
        jdbcTemplate.update((Connection conn) -> {
            PreparedStatement statement = conn.prepareStatement(
                    INSERT_ORGANIZATION, Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, organization.getName());
            statement.setString(2, organization.getDescription());
            statement.setString(3, organization.getAddress());
            return statement;
        }, keyHolder);
        organization.setId(keyHolder.getKey().intValue());
        final String INSERT_SUPERHEROS_CONN = "INSERT INTO superheroOrganization "
                + "(organizationId, superheroId) VALUES (?,?);";
        for (Superhero superhero : organization.getMembers()) {
            jdbcTemplate.update(INSERT_SUPERHEROS_CONN, organization.getId(), superhero.getId());
        }
        return organization;
    }


    @Override
    public List<Organization> listOrganizations(int superheroId) {
        final String SELECT_ORGANIZATIONS_FOR_SUPERHERO = "SELECT o.* FROM organization o "
                + "INNER JOIN superheroOrganization ON superheroId = ?;";
        return jdbcTemplate.query(SELECT_ORGANIZATIONS_FOR_SUPERHERO,
                new OrganizationMapper());
    }

    private List<Superhero> getSuperheros(int organizationId) {
        final String SELECT_MEMBERS = "SELECT superhero.id, superhero.name, "
                + "superhero.description, superpower.id, superpower.name "
                + "FROM superhero INNER JOIN superpower ON superhero.superpower = superpower.id "
                + "INNER JOIN superheroOrganization so ON so.superheroId = superhero.id "
                + "WHERE so.organizationId = ?;";
        return jdbcTemplate.query(SELECT_MEMBERS, new SuperheroDaoDatabaseImpl.SuperheroMapper(), organizationId);
    }


    private final class OrganizationMapper implements RowMapper<Organization> {

        @Override
        public Organization mapRow(ResultSet resultSet, int i) throws SQLException {
            Organization organization = new Organization();

            organization.setId(resultSet.getInt("id"));
            organization.setName(resultSet.getString("name"));
            organization.setDescription(resultSet.getString("description"));
            organization.setAddress(resultSet.getString("address"));

            return organization;
        }
    }
}

