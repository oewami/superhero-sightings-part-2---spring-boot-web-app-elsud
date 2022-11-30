/*package org.sh.dao;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sh.dto.Location;
import org.sh.dto.Sighting;
import org.sh.dto.Superhero;
import org.sh.dto.Superpower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SightingDaoImplTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SuperpowerDao superpowerDao;

    @Autowired
    SuperheroDao superheroDao;

    @Autowired
    SightingDao sightingDao;

    @Autowired
    LocationDao locationDao;

    @Before
    public void setUp() {
        final String DELETE_SIGHTINGS = "DELETE FROM sighting;";
        final String DELETE_SUPERHERO_ORGANIZATION = "DELETE FROM superheroOrganization;";
        final String DELETE_SUPERHERO = "DELETE FROM superhero;";
        final String DELETE_SUPERPOWER = "DELETE FROM superpower;";
        jdbcTemplate.update(DELETE_SIGHTINGS);
        jdbcTemplate.update(DELETE_SUPERHERO_ORGANIZATION);
        jdbcTemplate.update(DELETE_SUPERHERO);
        jdbcTemplate.update(DELETE_SUPERPOWER);
    }

    @Test
    public void getNotExistingSighting() {
        assertNull(sightingDao.getSighting(12));
    }

    @Test
    public void addAndGetSighting() {
        Location location = createLocation();
        Sighting sighting = new Sighting(
                "name", "description", location.getId(), location.getName());
        Sighting addedSighting = null;
        try {
            addedSighting = sightingDao.addSighting(sighting);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
        assertEquals(sighting, addedSighting);
        Sighting receivedSighting = sightingDao.getSighting(sighting.getId());
        assertEquals(addedSighting, receivedSighting);
    }

    @Test
    public void addSightingWithNotUniqueName() {
        Location location = createLocation();
        Sighting sighting = new Sighting(
                "name", "description", location.getId(), location.getName()
        );
        try {
            sightingDao.addSighting(sighting);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
        assertThrows(NotUniqueException.class, ()-> sightingDao.addSighting(sighting));
    }

    @Test
    public void listSightings() {
        Location location = createLocation();
        Sighting sighting1 = new Sighting(
                "name1", "description1", location.getId(), location.getName()
        );
        Sighting sighting2 = new Sighting(
                "name2", "description2", location.getId(), location.getName()
        );
        try {
            sightingDao.addSighting(sighting1);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
        List<Sighting> sightingList = sightingDao.listSightings();
        assertEquals(1, sightingList.size());
        assertTrue(sightingList.contains(sighting1));
        assertFalse(sightingList.contains(sighting2));
        try {
            sightingDao.addSighting(sighting2);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
        sightingList = sightingDao.listSightings();
        assertEquals(2, sightingList.size());
        assertTrue(sightingList.contains(sighting2));
    }

    @Test
    public void editSighting() {
        Location location = createLocation();
        Sighting sighting = new Sighting(
                "name", "description1", location.getId(), location.getName()
        );
        try {
            sighting = sightingDao.addSighting(sighting);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
        Sighting receivedSighting = sightingDao.getSighting(sighting.getId());
        assertEquals(sighting, receivedSighting);
        //sighting.setLocation("location");
        assertNotEquals(sighting, receivedSighting);
        try {
            boolean isEdited = sightingDao.editSighting(sighting);
            assertTrue(isEdited);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
        assertEquals(sighting, sightingDao.getSighting(sighting.getId()));
    }

    @Test
    public void editSightingWithNotUniqueName() {
        Location location = createLocation();
        Sighting sighting = new Sighting(
                "name1", "description1", location.getId(), location.getName()
        );
        Sighting sighting2 = new Sighting(
                "name2", "description2", location.getId(), location.getName()
        );
        try {
            sighting = sightingDao.addSighting(sighting);
            sightingDao.addSighting(sighting2);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
        Sighting receivedSighting = sightingDao.getSighting(sighting.getId());
        assertEquals(sighting, receivedSighting);
        sighting.setLocation(sighting2.getLocation());
        try {
            sightingDao.editSighting(sighting);
            fail("Should fail for not unique name");
        } catch (NotUniqueException ex) {
        }
    }

    @Test
    public void editNotExistingSighting() {
        Location location = createLocation();
        Sighting sighting = new Sighting(
                "name", "description", location.getId(), location.getName()
        );
        try {
            boolean isEdited = sightingDao.editSighting(sighting);
            assertFalse(isEdited);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique name");
        }
    }

    @Test
    public void deleteNotExistingSighting() {
        assertFalse(sightingDao.deleteSighting(12));
    }

    @Test
    public void deleteSighting() {
        // add sighting
        Location location = createLocation();
        Sighting sighting = new Sighting(
                "name", "description", location.getId(), location.getName()
        );
        try {
            sighting = sightingDao.addSighting(sighting);
        } catch (NotUniqueException ex) {
            fail("Shouldn't fail for unique name");
        }
        // test that sighting exists
        Sighting receivedSighting = sightingDao.getSighting(sighting.getId());
        assertNotNull(receivedSighting);
        assertEquals(sighting, receivedSighting);
        // delete and test that line was deleted
        boolean isDeleted = sightingDao.deleteSighting(sighting.getId());
        assertTrue(isDeleted);
        // test that sighting doesn't exist
        receivedSighting = sightingDao.getSighting(sighting.getId());
        assertNull(receivedSighting);
    }

    private Location createLocation() {
        Location location = new Location();
        location = locationDao.addLocation(location);
        return location;
    }

}
*/