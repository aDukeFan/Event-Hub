package ru.luckyskeet.main.stats;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.luckyskeet.client.stats.StatsClient;
import ru.luckyskeet.dto.stats.EndpointHitDto;
import ru.luckyskeet.dto.stats.ViewStatsDto;
import ru.luckyskeet.main.dto.EventFullDto;
import ru.luckyskeet.main.dto.EventShortDto;
import ru.luckyskeet.main.model.Event;
import ru.luckyskeet.main.util.constants_and_enums.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class StatsManagerImpl implements StatsManager {

    private final StatsClient client;

    private final String app = "ewm-main-service";

    @Override
    public void sendUriView(HttpServletRequest servletRequest) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT));
        client.save(new EndpointHitDto()
                .setIp(servletRequest.getRemoteAddr())
                .setUri(servletRequest.getRequestURI())
                .setApp(app)
                .setTimestamp(timestamp));
    }

    @Override
    public void sendEventView(Event event, HttpServletRequest servletRequest) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT));
        client.save(new EndpointHitDto()
                .setIp(servletRequest.getRemoteAddr())
                .setUri("EVENT WITH ID: " + event.getId() + " VIA LINK " + servletRequest.getRequestURI())
                .setApp(app)
                .setTimestamp(timestamp));
    }

    @Override
    public Integer getUniqueEventViews(Event event) {
        String start = event.getCreatedOn()
                .format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT));
        String end = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT));
        String getByIdUri = "EVENT WITH ID: " + event.getId() + " VIA LINK /events/" + event.getId();
        List<ViewStatsDto> viewStatsDtoList = client
                .get(app, start, end, List.of(getByIdUri), true);
        AtomicReference<Integer> count = new AtomicReference<>(0);
        viewStatsDtoList.forEach(viewStatsDto -> count.updateAndGet(v -> v + viewStatsDto.getHits()));
        return count.get();
    }

    @Override
    public List<EventShortDto> addViewsToShortDtosAndGetDtoList(List<Event> eventList,
                                                                List<EventShortDto> eventShortDtoList) {
        Map<Integer, Integer> idViews = getEventIdUniqueViewsCountMap(eventList);
        eventShortDtoList.forEach(eventShortDto -> eventShortDto.setViews(idViews.get(eventShortDto.getId())));
        return eventShortDtoList;
    }

    @Override
    public List<EventFullDto> addViewsToFullDtosAndGetDtoList(List<Event> eventList,
                                                               List<EventFullDto> eventFullDtoList) {
        Map<Integer, Integer> idViews = getEventIdUniqueViewsCountMap(eventList);
        eventFullDtoList.forEach(eventShortDto -> eventShortDto.setViews(idViews.get(eventShortDto.getId())));
        return eventFullDtoList;
    }

    private Map<Integer, Integer> getEventIdUniqueViewsCountMap(List<Event> events) {
        Map<Integer, Integer> idViews = new HashMap<>();
        events.forEach(event -> idViews.put(event.getId(), getUniqueEventViews(event)));
        return idViews;
    }
}