package ru.luckyskeet.main.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.luckyskeet.main.model.Location;
import ru.luckyskeet.main.repository.LocationRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public Location saveOrGetLocation(float lon, float lat) {
        Optional<Location> optionalLocation = locationRepository.findByLatAndLon(lat, lon);
        if (optionalLocation.isPresent()) {
            return optionalLocation.get();
        } else {
            Location location = new Location()
                    .setLat(lat)
                    .setLon(lon);
            return locationRepository.save(location);
        }
    }
}
