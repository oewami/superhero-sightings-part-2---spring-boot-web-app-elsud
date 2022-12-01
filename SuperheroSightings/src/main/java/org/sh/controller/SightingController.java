package org.sh.controller;

import org.sh.dao.*;
import org.sh.dto.Location;
import org.sh.dto.Sighting;
import org.sh.dto.Superhero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/sighting")
public class SightingController {

    private final SightingDao sightingDao;
    private final SuperheroDao superheroDao;
    private final LocationDao locationDao;

    @Autowired
    public SightingController(SightingDao sightingDao, SuperheroDao superheroDao, LocationDao locationDao) {
        this.sightingDao = sightingDao;
        this.superheroDao = superheroDao;
        this.locationDao = locationDao;
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
    public ResponseEntity updateSighting(@PathVariable int id, @RequestBody SightingFromRequest sighting) throws NotUniqueException {
        Superhero superhero = superheroDao.getSuperhero(sighting.getSuperheroId());
        Location location = locationDao.getLocation(sighting.getLocationId());
        if (superhero == null || location == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        Sighting newSighting = new Sighting();
        newSighting.setId(id);
        newSighting.setDate(sighting.getDate());
        newSighting.setLocation(location);
        newSighting.setSuperhero(superhero);
        if (sightingDao.editSighting(newSighting)) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Sighting> addSighting(@RequestBody SightingFromRequest sighting) throws NotUniqueException {
        if (sighting.getDate().isAfter(LocalDate.now())) {
            throw new DateTimeException("Date cannot be in the future");
        }
        Superhero superhero = superheroDao.getSuperhero(sighting.getSuperheroId());
        Location location = locationDao.getLocation(sighting.getLocationId());
        if (superhero == null || location == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        Sighting newSighting = new Sighting();
        newSighting.setDate(sighting.getDate());
        newSighting.setLocation(location);
        newSighting.setSuperhero(superhero);
        return new ResponseEntity<>(sightingDao.addSighting(newSighting), HttpStatus.CREATED);
    }

    private static class SightingFromRequest {
        private int id;
        private int locationId;
        private int superheroId;
        private LocalDate date;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLocationId() {
            return locationId;
        }

        public void setLocationId(int locationId) {
            this.locationId = locationId;
        }

        public int getSuperheroId() {
            return superheroId;
        }

        public void setSuperheroId(int superheroId) {
            this.superheroId = superheroId;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = LocalDate.parse(date);
        }

    }

}