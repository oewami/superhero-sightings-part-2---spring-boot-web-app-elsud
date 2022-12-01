package org.sh.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sh.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SuperheroDaoDatabaseImplTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SuperpowerDao superpowerDao;

    @Autowired
    SuperheroDao superheroDao;

    @Autowired
    OrganizationDao organizationDao;

    @Autowired
    LocationDao locationDao;

    @Autowired
    SightingDao sightingDao;

    @Before
    public void setUp() {
        final String DELETE_SIGHTINGS = "DELETE FROM sighting;";
        final String DELETE_SUPERHERO_ORGANIZATION = "DELETE FROM superheroOrganization;";
        final String DELETE_LOCATION = "DELETE FROM location;";
        final String DELETE_ORGANIZATION = "DELETE FROM organization;";
        final String DELETE_SUPERHERO = "DELETE FROM superhero;";
        final String DELETE_SUPERPOWER = "DELETE FROM superpower;";

        jdbcTemplate.update(DELETE_SIGHTINGS);
        jdbcTemplate.update(DELETE_LOCATION);
        jdbcTemplate.update(DELETE_SUPERHERO_ORGANIZATION);
        jdbcTemplate.update(DELETE_ORGANIZATION);
        jdbcTemplate.update(DELETE_SUPERHERO);
        jdbcTemplate.update(DELETE_SUPERPOWER);
    }

    @Test
    public void getNotExistingSuperhero() {
        assertNull(superheroDao.getSuperhero(12));
    }

    @Test
    public void getAndAddSuperhero() {
        Superpower superpower = createSuperpower();
        Superhero superhero = new Superhero(
                "name", "description", superpower.getId(), superpower.getName());
        Superhero addedSuperhero = null;
        try {
            addedSuperhero = superheroDao.addSuperhero(superhero);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
        assertEquals(superhero, addedSuperhero);
        Superhero receivedSuperhero = superheroDao.getSuperhero(superhero.getId());
        assertEquals(addedSuperhero, receivedSuperhero);
    }

    @Test
    public void addSuperheroWithNotUniqueName() {
        Superpower superpower = createSuperpower();
        Superhero superhero = new Superhero(
                "name", "description", superpower.getId(), superpower.getName()
        );
        try {
            superheroDao.addSuperhero(superhero);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
        assertThrows(NotUniqueException.class, ()-> superheroDao.addSuperhero(superhero));
    }

    @Test
    public void listSuperhero() {
        Superpower superpower = createSuperpower();
        Superhero superhero1 = new Superhero(
                "name1", "description1", superpower.getId(), superpower.getName()
        );
        Superhero superhero2 = new Superhero(
                "name2", "description2", superpower.getId(), superpower.getName()
        );
        try {
            superheroDao.addSuperhero(superhero1);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
        List<Superhero> superheroList = superheroDao.listSuperhero();
        assertEquals(1, superheroList.size());
        assertTrue(superheroList.contains(superhero1));
        assertFalse(superheroList.contains(superhero2));
        try {
            superheroDao.addSuperhero(superhero2);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
        superheroList = superheroDao.listSuperhero();
        assertEquals(2, superheroList.size());
        assertTrue(superheroList.contains(superhero2));
    }

    @Test
    public void editSuperhero() {
        Superpower superpower = createSuperpower();
        Superhero superhero = new Superhero(
                "name", "description1", superpower.getId(), superpower.getName()
        );
        try {
            superhero = superheroDao.addSuperhero(superhero);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
        Superhero receivedSuperhero = superheroDao.getSuperhero(superhero.getId());
        assertEquals(superhero, receivedSuperhero);
        superhero.setName("newName");
        superhero.setDescription("newDescription");
        assertNotEquals(superhero, receivedSuperhero);
        try {
            boolean isEdited = superheroDao.editSuperhero(superhero);
            assertTrue(isEdited);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
        assertEquals(superhero, superheroDao.getSuperhero(superhero.getId()));
    }

    @Test
    public void editSuperheroWithNotUniqueName() {
        Superpower superpower = createSuperpower();
        Superhero superhero = new Superhero(
                "name1", "description1", superpower.getId(), superpower.getName()
        );
        Superhero superhero2 = new Superhero(
                "name2", "description2", superpower.getId(), superpower.getName()
        );
        try {
            superhero = superheroDao.addSuperhero(superhero);
            superheroDao.addSuperhero(superhero2);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
        Superhero receivedSuperhero = superheroDao.getSuperhero(superhero.getId());
        assertEquals(superhero, receivedSuperhero);
        superhero.setName(superhero2.getName());
        try {
            superheroDao.editSuperhero(superhero);
            fail("Should fail for not unique name");
        } catch (NotUniqueException ex) {
        }
    }

    @Test
    public void editNotExistingSuperhero() {
        Superpower superpower = createSuperpower();
        Superhero superhero = new Superhero(
                12, "name", "description", superpower
        );
        try {
            boolean isEdited = superheroDao.editSuperhero(superhero);
            assertFalse(isEdited);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
    }

    @Test
    public void deleteNotExistingSuperhero() {
        assertFalse(superheroDao.deleteSuperhero(12));
    }

    @Test
    public void deleteSuperhero() {
        int[] ids = createSuperheroWithLocationOrganizationSighting();
        int superpowerId = ids[0];
        int superheroId = ids[1];
        int organizationId = ids[2];
        int locationId = ids[3];
        int sightingId = ids[4];
        assertNotNull(superheroDao.getSuperhero(superheroId));
        assertTrue(superheroDao.deleteSuperhero(superheroId));
        assertFalse(superheroDao.deleteSuperhero(superheroId));
        assertNotNull(superpowerDao.getSuperpower(superpowerId));
        assertNotNull(locationDao.getLocation(locationId));
        assertNotNull(organizationDao.getOrganization(organizationId));
        assertNull(sightingDao.getSighting(sightingId));
        assertTrue(superheroDao.listSuperheroForOrganization(organizationId).isEmpty());
        assertTrue(organizationDao.getOrganization(organizationId).getMembers().isEmpty());
    }

    @Test
    public void listSuperheroForLocation() {
        int[] ids = createSuperheroWithLocationOrganizationSighting();
        int superheroId = ids[1];
        int locationId = ids[3];
        int sightingId = ids[4];
        List<Superhero> superheros = superheroDao.listSuperheroForLocation(locationId);
        Superhero superhero = superheroDao.getSuperhero(superheroId);
        assertFalse(superheros.isEmpty());
        assertEquals(1, superheros.size());
        assertEquals(superhero, superheros.get(0));
        sightingDao.deleteSighting(sightingId);
        assertTrue(superheroDao.listSuperheroForLocation(locationId).isEmpty());
    }

    @Test
    public void listSuperheroForOrganization() {
        int[] ids = createSuperheroWithLocationOrganizationSighting();
        int superheroId = ids[1];
        int organizationId = ids[2];
        List<Superhero> superheros = superheroDao.listSuperheroForOrganization(organizationId);
        Superhero superhero = superheroDao.getSuperhero(superheroId);
        assertFalse(superheros.isEmpty());
        assertEquals(1, superheros.size());
        assertEquals(superhero, superheros.get(0));
        Organization organization = organizationDao.getOrganization(organizationId);
        organization.setMembers(new ArrayList<>());
        organizationDao.editOrganization(organization);
        assertTrue(superheroDao.listSuperheroForOrganization(organizationId).isEmpty());
    }

    private Superpower createSuperpower() {
        Superpower superpower = new Superpower("power");
        try {
            superpower = superpowerDao.addSuperpower(superpower);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique superpower");
        }
        return superpower;
    }

    private int[] createSuperheroWithLocationOrganizationSighting() {
        // create superhero
        Superpower superpower = createSuperpower();
        Superhero superhero = new Superhero(
                "name", "description", superpower.getId(), superpower.getName()
        );
        try {
            superhero = superheroDao.addSuperhero(superhero);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
        // create organization
        List<Superhero> superheros = new ArrayList<>();
        superheros.add(superhero);
        Organization organization = new Organization();
        organization.setName("org");
        organization.setMembers(superheros);
        organization = organizationDao.addOrganization(organization);
        // create location
        Location location = new Location();
        location.setName("loc");
        location.setAddress("addr");
        location.setLatitude(new BigDecimal("23.456"));
        location.setLongitude(new BigDecimal("23.456"));
        location = locationDao.addLocation(location);
        // create sighting
        Sighting sighting = new Sighting();
        sighting.setSuperhero(superhero);
        sighting.setLocation(location);
        sighting.setDate(LocalDate.now());
        try {
            sighting = sightingDao.addSighting(sighting);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique sighting");
        }
        return new int[]{superpower.getId(), superhero.getId(), organization.getId(), location.getId(), sighting.getId()};

    }
}