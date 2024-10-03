package ru.luckyskeet.main.stats;

import jakarta.servlet.http.HttpServletRequest;
import ru.luckyskeet.main.dto.EventFullDto;
import ru.luckyskeet.main.dto.EventShortDto;
import ru.luckyskeet.main.model.Event;

import java.util.List;

public interface StatsManager {

    void sendUriView(HttpServletRequest servletRequest);

    void sendEventView(Event event, HttpServletRequest servletRequest);

    Integer getUniqueEventViews(Event event);

    List<EventShortDto> addViewsToShortDtosAndGetDtoList(List<Event> eventList,
                                                         List<EventShortDto> eventShortDtoList);

    List<EventFullDto> addViewsToFullDtosAndGetDtoList(List<Event> eventList,
                                                       List<EventFullDto> eventFullDtoList);
}
