package ru.luckyskeet.main.service;

import ru.luckyskeet.main.model.Location;

public interface LocationService {

    Location saveOrGetLocation(float lon, float lat);
}
