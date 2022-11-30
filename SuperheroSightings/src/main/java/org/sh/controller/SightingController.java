package org.sh.controller;

import org.sh.dao.DeletionException;
import org.sh.dao.NotUniqueException;
import org.sh.dao.SightingDao;
import org.sh.dao.SuperpowerDao;
import org.sh.dto.Sighting;
import org.sh.dto.Superpower;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/superpower")
public class SightingController {

    private final SightingDao sightingDao;

    @Autowired
    public SightingController(SightingDao sightingDao) {
        this.sightingDao = sightingDao;
    }

    @GetMapping("")
    public List<Sighting> listSightings() {
        return sightingDao.listSightings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sighting> getSighting(@PathVariable int id) {
        Sighting sighting = sightingDao.getSighting(id);
        if (sighting == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(sighting);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteSighting(@PathVariable int id) throws DeletionException {
        if (sightingDao.deleteSighting(id)) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateSighting(@PathVariable int id, @RequestBody Sighting sighting) throws NotUniqueException {
        sighting.setId(id);
        if (sightingDao.editSighting(sighting)) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Sighting addSighting(@RequestBody Sighting sighting) throws NotUniqueException {
        return sightingDao.addSighting(sighting);
    }

}
