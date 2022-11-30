package org.sh.controller;

import org.sh.dao.DeletionException;
import org.sh.dao.NotUniqueException;
import org.sh.dao.OrganizationDao;
import org.sh.dao.SuperpowerDao;
import org.sh.dto.Organization;
import org.sh.dto.Superpower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/superpower")
public class OrganizationController {

    private final OrganizationDao orgDao;

    @Autowired
    public OrganizationController(OrganizationDao orgDao) {
        this.orgDao = orgDao;
    }

    @GetMapping("")
    public List<Organization> listOrganizations() {
        return orgDao.listOrganizations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Organization> getOrganization(@PathVariable int id) {
        Organization organization = orgDao.getOrganization(id);
        if (organization == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(organization);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteOrganization(@PathVariable int id) throws DeletionException {
        if (orgDao.deleteOrganization(id)) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateOrganization(@PathVariable int id, @RequestBody Organization organization) throws NotUniqueException {
        organization.setId(id);
        if (orgDao.editOrganization(organization)) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Organization addOrganization(@RequestBody Organization organization) throws NotUniqueException {
        return orgDao.addOrganization(organization);
    }

}
