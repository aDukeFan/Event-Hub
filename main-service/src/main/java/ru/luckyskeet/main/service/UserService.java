package ru.luckyskeet.main.service;

import ru.luckyskeet.main.dto.EventFullDto;
import ru.luckyskeet.main.dto.EventShortDto;
import ru.luckyskeet.main.dto.NewEventDto;
import ru.luckyskeet.main.dto.ParticipationRequestDto;
import ru.luckyskeet.main.dto.RatingDto;
import ru.luckyskeet.main.request.EventRequestStatusUpdateRequest;
import ru.luckyskeet.main.request.EventRequestStatusUpdateResult;
import ru.luckyskeet.main.request.UpdateEventRequest;

import java.util.List;

public interface UserService {

    EventFullDto saveEvent(Integer userId,
                           NewEventDto newEventDto);

    List<EventShortDto> getEventsByUserId(Integer userId,
                                          Integer from, Integer size);

    EventFullDto getEventByUserIdAndEventId(Integer userId,
                                            Integer eventId);

    EventFullDto updateEventByUserIdAndEventId(Integer userId,
                                               Integer eventId,
                                               UpdateEventRequest request);

    List<ParticipationRequestDto> getEventsRequests(Integer userId,
                                                    Integer eventId);

    EventRequestStatusUpdateResult setRequestStatus(Integer userId,
                                                    Integer eventId,
                                                    EventRequestStatusUpdateRequest request);

    List<ParticipationRequestDto> getAllUserRequests(Integer userId);

    ParticipationRequestDto saveNewRequest(Integer userId,
                                           Integer eventId);

    ParticipationRequestDto canselRequest(Integer userId,
                                          Integer requestId);

    RatingDto saveRating(Integer userId, Integer eventId, Boolean isLike);

    RatingDto updateRating(Integer userId, Integer eventId, Boolean isLike);
}
