/*package org.sh.dao;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.Or;
import org.sh.dto.Organization;
import org.sh.dto.Superhero;
import org.sh.dto.Superpower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class OrganizationDaoImplTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SuperpowerDao superpowerDao;

    @Autowired
    SuperheroDao superheroDao;

    @Autowired
    OrganizationDao organizationDao;

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
    public void getNotExistingOrganization() {
        assertNull(organizationDao.getOrganization(12));
    }

    @Test
    public void getAndAddOrganization() {
        /// test adding unique organization
        Organization organization = new Organization();
        Organization addedOrganization = null;
        try {
            addedOrganization = organizationDao.addOrganization(organization);
        } catch (NotUniqueException ex) {
            fail("Shouldn't fail for unique name");
        }
        Organization receivedOrganization = organizationDao.getOrganization(addedOrganization.getId());
        assertEquals(addedOrganization, receivedOrganization);
        assertEquals(organization.getName(), receivedOrganization.getName());
        // test adding not unique organization
        assertThrows(NotUniqueException.class, () -> organizationDao.addOrganization(organization));
    }

    /*
    @Test
    //review
    public void addOrganizationWithNotUniqueName() {
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

     */
/*
    @Test
    public void listOrganization() {

        Organization organization1 = new Organization();
        Organization organization2 = new Organization();
        // test after adding 1
        try {
            organizationDao.addOrganization(organization1);
        } catch (NotUniqueException ex) {
            fail("Shouldn't fail for unique name");
        }
        List<Organization> organizationList = organizationDao.listOrganizations();
        assertEquals(1, organizationList.size());
        assertTrue(organizationList.contains(organization1));
        assertFalse(organizationList.contains(organization2));
        // test after adding both
        try {
            organizationDao.addOrganization(organization2);
        } catch (NotUniqueException ex) {
            fail("Shouldn't fail for unique name");
        }
        organizationList = organizationDao.listOrganizations();
        assertEquals(2, organizationList.size());
        assertTrue(organizationList.contains(organization2));
    }

    @Test
    public void editOrganization() {
        // add organization
        Organization organization = new Organization();
        Organization addedOrganization = null;
        try {
            addedOrganization = organizationDao.addOrganization(organization);
        } catch (NotUniqueException ex) {
            fail("Shouldn't fail for unique name");
        }
        // edit organization
        addedOrganization.setName("newName");
        // check that organization in db has previous name
        Organization receivedOrganization = organizationDao.getOrganization(addedOrganization.getId());
        assertNotEquals(addedOrganization.getName(), receivedOrganization.getName());
        // edit organization in db and test that names are similar
        try {
            organizationDao.editOrganization(addedOrganization);
            receivedOrganization = organizationDao.getOrganization(addedOrganization.getId());
        } catch (NotUniqueException ex) {
            fail("Shouldn't fail for unique name");
        }
        assertEquals(addedOrganization.getName(), receivedOrganization.getName());
    }

    @Test
    public void editOrganizationWithNotUniqueName() {
        Organization organization = null;
        try {
            // add organization with "testName"
            organization = organizationDao.addOrganization(new Organization());
            // add superpower with "newName"
            organizationDao.addOrganization(new Organization());
        } catch (NotUniqueException ex) {
            fail("Shouldn't fail for unique name");
        }
        // set "testName" to "newName" and test that it cannot be added to db
        organization.setName("newName");
        try {
            organizationDao.editOrganization(organization);
            fail("Should fail for not unique name");
        } catch (NotUniqueException ex) {
        }
    }

    @Test
    public void editNotExistingOrganization() {
        // test that no line was modified
        Organization organization = new Organization();
        organization.setId(15);
        try {
            boolean isModified = organizationDao.editOrganization(organization);
            assertFalse(isModified);
        } catch (NotUniqueException ex) {
            fail("Shouldn't fail for unique name");
        }
    }

    @Test
    public void deleteNotExistingOrganization() {
        assertFalse(organizationDao.deleteOrganization(12));
    }

    @Test
    public void deleteOrganization() {
        // add superpower
        Organization organization = new Organization();
        try {
            organization = organizationDao.addOrganization(organization);
        } catch (NotUniqueException ex) {
            fail("Shouldn't fail for unique name");
        }
        // test that superpower exists
        Organization receivedOrganization = organizationDao.getOrganization(organization.getId());
        assertNotNull(receivedOrganization);
        assertEquals(organization, receivedOrganization);
        // delete and test that line was deleted
        boolean isDeleted = organizationDao.deleteOrganization(organization.getId());
        assertTrue(isDeleted);
    }

    @Test
    public void listOrganizationForLocation() {
    }

    @Test
    public void listOrganizationForOrganization() {
    }

    private Superpower createOrganization() {
        Superpower superpower = new Superpower("power");
        try {
            superpower = superpowerDao.addSuperpower(superpower);
        } catch (NotUniqueException ex) {
            fail("Should not fail for unique superpower");
        }
        return superpower;
    }
}
*/