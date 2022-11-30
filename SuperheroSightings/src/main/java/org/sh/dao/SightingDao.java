package org.sh.dao;

import org.sh.dto.Sighting;

import java.time.LocalDate;
import java.util.List;

public interface SightingDao {

    public Sighting getSighting(int sightingId);

    public List<Sighting> listSightings();

    public boolean editSighting(Sighting sighting) throws NotUniqueException;

    public boolean deleteSighting(int sightingId);

    public Sighting addSighting(Sighting sighting) throws NotUniqueException;

    public List<Sighting> listSightings(LocalDate date);

    public List<Sighting> listLastSightings(); // last 10 sightings

}