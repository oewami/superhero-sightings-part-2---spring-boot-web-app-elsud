package org.sh.controller;

import org.sh.dao.DeletionException;
import org.sh.dao.LocationDao;
import org.sh.dto.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/location")
public class LocationController {

    private final LocationDao locationDao;

    @Autowired
    public LocationController(LocationDao locationDao) { this.locationDao = locationDao; }

    @GetMapping("")
    public List<Location> listLocation() {
        return locationDao.listLocations();
    }


    @GetMapping("/superhero/{superheroId}")
    public List<Location> listSuperheroLocations(@PathVariable int superheroId) {
        return locationDao.listSuperheroLocations(superheroId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocation(@PathVariable int id) {
        Location location = locationDao.getLocation(id);
        if (location != null) {
            return ResponseEntity.ok(location);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("")
    public Location addLocation(@RequestBody Location location) {
        return locationDao.addLocation(location);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteLocation(@PathVariable int id) throws DeletionException {
        if(locationDao.deleteLocation(id)) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateLocation(@PathVariable int id, @RequestBody Location location) {
        location.setId(id);
        if(locationDao.editLocation(location)) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
