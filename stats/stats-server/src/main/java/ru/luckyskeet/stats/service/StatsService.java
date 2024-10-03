package ru.luckyskeet.stats.service;

import ru.luckyskeet.dto.stats.EndpointHitDto;
import ru.luckyskeet.dto.stats.ViewStatsDto;

import java.util.List;

public interface StatsService {

    void save(EndpointHitDto hitDto);

    List<ViewStatsDto> get(String app,
                           String start,
                           String end,
                           List<String> uris,
                           Boolean unique);

}
