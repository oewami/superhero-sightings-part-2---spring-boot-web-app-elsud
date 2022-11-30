package org.sh.controller;

import org.sh.dao.*;
import org.sh.dto.Organization;
import org.sh.dto.Superhero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/organization")
public class OrganizationController {

    private final OrganizationDao orgDao;

    private final SuperheroDao sphDao;

    @Autowired
    public OrganizationController(OrganizationDao orgDao, SuperheroDao sphDao) {
        this.orgDao = orgDao;
        this.sphDao = sphDao;
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
    public ResponseEntity deleteOrganization(@PathVariable int id) {
        if (orgDao.deleteOrganization(id)) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateOrganization(
            @PathVariable int id, @RequestBody OrganizationFromRequest org) {
        // fill members
        List<Superhero> members = new ArrayList<>();
        Organization organization = new Organization();
        for (int memberId : org.getMembers()) {
            Superhero member = sphDao.getSuperhero(memberId);
            if (member == null) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            members.add(member);
        }
        // fill organization
        organization.setMembers(members);
        organization.setId(id);
        organization.setName(org.getName());
        organization.setAddress(org.getAddress());
        organization.setDescription(org.getDescription());

        if (orgDao.editOrganization(organization)) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PostMapping("")
    public ResponseEntity<Organization> addOrganization(@RequestBody OrganizationFromRequest org) {
        // fill members
        List<Superhero> members = new ArrayList<>();
        Organization organization = new Organization();
        for (int memberId : org.getMembers()) {
            Superhero member = sphDao.getSuperhero(memberId);
            if (member == null) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            members.add(member);
        }
        // fill organization
        organization.setMembers(members);
        organization.setName(org.getName());
        organization.setAddress(org.getAddress());
        organization.setDescription(org.getDescription());

        return new ResponseEntity<>(orgDao.addOrganization(organization), HttpStatus.CREATED);
    }

    private static class OrganizationFromRequest {
         private int id;
        private String name;
        private String description;
        private String address;
        private List<Integer> members;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public List<Integer> getMembers() {
            if (members == null) {
                return new ArrayList<>();
            }
            return members;
        }

        public void setMembers(List<Integer> members) {
            this.members = members;
        }
    }

}
