package org.sh.dao;

import org.sh.dto.Sighting;

import java.time.LocalDateTime;
import java.util.List;

public interface SightingDao {

    public Sighting getSighting(int sightingId);

    public List<Sighting> listSightings();

    public boolean editSighting(Sighting sighting);

    public boolean deleteSighting(int sightingId);

    public Sighting addSighting(Sighting sighting);

    public List<Sighting> listSightings(LocalDateTime date);

    public List<Sighting> listLastSightings(); // last 10 sightings

}