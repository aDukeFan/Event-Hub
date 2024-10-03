package ru.luckyskeet.stats.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.luckyskeet.dto.stats.EndpointHitDto;
import ru.luckyskeet.dto.stats.ViewStatsDto;
import ru.luckyskeet.stats.mapper.EndpointHitMapper;
import ru.luckyskeet.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class StatsServiceImp implements StatsService {

    private final StatsRepository repository;
    private final EndpointHitMapper mapper;

    @Override
    public void save(EndpointHitDto hitDto) {
        repository.save(mapper.toSave(hitDto));
    }

    @Override
    public List<ViewStatsDto> get(String app,
                                  String start,
                                  String end,
                                  List<String> uris,
                                  Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        if (startTime.isAfter(endTime)) {
            throw new NumberFormatException("Bad time frame");
        }

        List<ViewStatsDto> viewStatsList = new ArrayList<>();
        if (uris.isEmpty()) {
            uris.addAll(repository.findAllUrisWithParams(app, startTime, endTime));
        }

        if (unique) {
            uris.forEach(uri -> viewStatsList.add(
                    new ViewStatsDto()
                            .setUri(uri)
                            .setApp(app)
                            .setHits(repository
                                    .countUniqueIpWithParams(app, uri, startTime, endTime))));
        } else {
            uris.forEach(uri -> viewStatsList.add(
                    new ViewStatsDto()
                            .setApp(app)
                            .setUri(uri)
                            .setHits(repository
                                    .countByAppAndUriEndingWithAndTimestampBetween(app, uri, startTime, endTime))));
        }

        viewStatsList.sort(Comparator.comparing(ViewStatsDto::getHits).reversed());
        return viewStatsList;
    }
}
