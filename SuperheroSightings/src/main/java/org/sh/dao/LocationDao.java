package org.sh.dao;

import org.sh.dto.Location;

import java.util.List;

public interface LocationDao {

    public Location getLocation(int locationId);

    public List<Location> listLocations();

    public boolean editLocation(Location location);

    public boolean deleteLocation(int locationId) throws DeletionException;

    public Location addLocation(Location location);
    
    public List<Location> listSuperheroLocations(int superheroId);

}
