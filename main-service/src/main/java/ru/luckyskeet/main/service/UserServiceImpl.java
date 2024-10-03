package ru.luckyskeet.main.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.luckyskeet.main.dto.EventFullDto;
import ru.luckyskeet.main.dto.EventShortDto;
import ru.luckyskeet.main.dto.NewEventDto;
import ru.luckyskeet.main.dto.ParticipationRequestDto;
import ru.luckyskeet.main.dto.RatingDto;
import ru.luckyskeet.main.exception.MaxLimitException;
import ru.luckyskeet.main.mapper.EventMapper;
import ru.luckyskeet.main.mapper.ParticipationRequestMapper;
import ru.luckyskeet.main.mapper.RatingMapper;
import ru.luckyskeet.main.model.Category;
import ru.luckyskeet.main.model.Event;
import ru.luckyskeet.main.model.Location;
import ru.luckyskeet.main.model.ParticipationRequest;
import ru.luckyskeet.main.model.Rating;
import ru.luckyskeet.main.model.User;
import ru.luckyskeet.main.repository.CategoryRepository;
import ru.luckyskeet.main.repository.EventRepository;
import ru.luckyskeet.main.repository.RatingRepository;
import ru.luckyskeet.main.repository.RequestRepository;
import ru.luckyskeet.main.repository.UserRepository;
import ru.luckyskeet.main.request.EventRequestStatusUpdateRequest;
import ru.luckyskeet.main.request.EventRequestStatusUpdateResult;
import ru.luckyskeet.main.request.UpdateEventRequest;
import ru.luckyskeet.main.stats.StatsManager;
import ru.luckyskeet.main.util.ServiceUtils;
import ru.luckyskeet.main.util.constants_and_enums.Constants;
import ru.luckyskeet.main.util.constants_and_enums.EventState;
import ru.luckyskeet.main.util.constants_and_enums.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;
    private final ParticipationRequestMapper requestMapper;
    private final LocationService locationService;

    private final StatsManager statsManager;
    private final ServiceUtils serviceUtils;

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    @Value("${feature.rating.dateCheck.enabled}")
    private boolean isDateCheckEnabled;

    @Override
    public EventFullDto saveEvent(Integer userId, NewEventDto newEventDto) {
        Event event = eventMapper.toSave(newEventDto);
        User user = userRepository.findById(userId).orElseThrow();
        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow();
        event.setCreatedOn(LocalDateTime.now());
        Location location = locationService
                .saveOrGetLocation(newEventDto.getLocation().getLon(),
                        newEventDto.getLocation().getLat());
        event.setLocation(location);
        event.setUser(user);
        event.setCategory(category);

        event.setState(EventState.PENDING);
        return eventMapper.toShowFull(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Integer userId, Integer from, Integer size) {
        Pageable pageable = serviceUtils.createPageable(from, size);
        List<Event> events = eventRepository.findAllByUserId(userId, pageable);
        List<EventShortDto> eventShortDtoList = events.stream().map(eventMapper::toShowShort).toList();
        return statsManager.addViewsToShortDtosAndGetDtoList(events, eventShortDtoList);
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Integer userId, Integer eventId) {
        Event event = eventRepository.findByIdAndUserId(eventId, userId);
        int views = statsManager.getUniqueEventViews(event);
        return eventMapper.toShowFull(event).setViews(views);
    }

    @Override
    public EventFullDto updateEventByUserIdAndEventId(Integer userId,
                                                      Integer eventId,
                                                      UpdateEventRequest request) {
        Event eventToUpdate = eventRepository.findByIdAndUserId(eventId, userId);
        if (eventToUpdate.getState() == EventState.PUBLISHED) {
            String message = String.format("Event with ID %d is already %s",
                    eventId, eventToUpdate.getState());
            throw new ValidationException(message);
        }

        serviceUtils.updateEventFields(eventToUpdate, request, categoryRepository);

        Optional.ofNullable(request.getStateAction())
                .ifPresent(stateAction -> {
                    if (stateAction.equals(Constants.USER_CANCEL_REVIEW)) {
                        eventToUpdate.setState(EventState.CANCELED);
                    } else if (stateAction.equals(Constants.USER_SEND_TO_REVIEW)) {
                        eventToUpdate.setState(EventState.PENDING);
                    }
                });

        Event savedEvent = eventRepository.save(eventToUpdate);
        int views = statsManager.getUniqueEventViews(savedEvent);
        return eventMapper.toShowFull(savedEvent).setViews(views);
    }

    @Override
    public List<ParticipationRequestDto> getEventsRequests(Integer userId, Integer eventId) {
        if (eventRepository.existsByIdAndUserId(eventId, userId)) {
            return requestRepository.findAllByEventId(eventId).stream()
                    .map(requestMapper::toDto)
                    .toList();
        }
        return List.of();
    }

    @Override
    public EventRequestStatusUpdateResult setRequestStatus(Integer userId,
                                                           Integer eventId,
                                                           EventRequestStatusUpdateRequest statusUpdateRequest) {

        Event event = eventRepository.findByIdAndUserId(eventId, userId);
        int maxLimit = event.getParticipantLimit();
        int confirmedCount = event.getConfirmedRequests();
        boolean requestModeration = event.isRequestModeration();
        List<Integer> requestIds = statusUpdateRequest.getRequestIds();
        RequestStatus newStatus = RequestStatus.valueOf(statusUpdateRequest.getStatus());

        // Проверка, требуется ли подтверждение заявок
        if (maxLimit != 0 && requestModeration) {
            if (confirmedCount == maxLimit && RequestStatus.CONFIRMED.equals(newStatus)) {
                throw new ValidationException("Too many confirmed requests");
            }
            if (requestIds.size() > (maxLimit - confirmedCount) && RequestStatus.CONFIRMED.equals(newStatus)) {
                throw new ValidationException("Too many requests");
            }
        }

        List<ParticipationRequest> requests = requestRepository.findAllByEventId(eventId);

        // Проверка статусов заявок
        for (ParticipationRequest request : requests) {
            if (request.getStatus().equals(RequestStatus.REJECTED)
                    || request.getStatus().equals(RequestStatus.CANCELED)) {
                throw new ValidationException("Cannot update requests with status REJECTED or CANCELED");
            }
            if (request.getStatus().equals(RequestStatus.CONFIRMED)
                    && newStatus.equals(RequestStatus.CANCELED)) {
                throw new ValidationException("Cannot cancel a CONFIRMED request");
            }
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ValidationException("Cannot update NOT PENDING request");
            }
        }

        // Обновление статусов заявок
        switch (newStatus) {
            case REJECTED -> requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
            case CONFIRMED -> {
                requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
                event.setConfirmedRequests(confirmedCount + requests.size());
                if (requests.size() == maxLimit - confirmedCount) {
                    List<ParticipationRequest> pendingRequests = requestRepository
                            .findAllByEventIdAndStatus(eventId, RequestStatus.PENDING);
                    pendingRequests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
                    requests.addAll(pendingRequests);
                }
                eventRepository.save(event);
            }
        }

        requestRepository.saveAll(requests);

        List<ParticipationRequest> allEventRequests = requestRepository.findAllByEventId(eventId);

        List<ParticipationRequestDto> confirmedRequests = allEventRequests.stream()
                .filter(request -> request.getStatus().equals(RequestStatus.CONFIRMED))
                .map(requestMapper::toDto)
                .toList();

        List<ParticipationRequestDto> rejectedRequests = allEventRequests.stream()
                .filter(request -> request.getStatus().equals(RequestStatus.REJECTED))
                .map(requestMapper::toDto)
                .toList();

        return new EventRequestStatusUpdateResult()
                .setConfirmedRequests(confirmedRequests)
                .setRejectedRequests(rejectedRequests);
    }

    @Override
    public List<ParticipationRequestDto> getAllUserRequests(Integer userId) {
        return requestRepository.findByRequesterId(userId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto saveNewRequest(Integer userId, Integer eventId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new ValidationException("Published event not found with id: " + eventId));

        if (Objects.equals(event.getUser().getId(), userId)) {
            throw new ValidationException("Initiator can't send request");
        }

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ValidationException("You may send request only once");
        }

        int limit = event.getParticipantLimit();
        int count = event.getConfirmedRequests();

        if (limit != 0 && limit == count) {
            throw new MaxLimitException("Too late, there are no places");
        }

        ParticipationRequest participationRequest = new ParticipationRequest()
                .setRequester(requester)
                .setEvent(event)
                .setCreated(LocalDateTime.now());

        if (limit == 0) {
            participationRequest.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(count + 1);
        } else if (event.isRequestModeration()) {
            participationRequest.setStatus(RequestStatus.PENDING);
        } else {
            participationRequest.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(count + 1);
        }

        participationRequest = requestRepository.save(participationRequest);
        eventRepository.save(event);

        return requestMapper.toDto(participationRequest);
    }

    @Override
    public ParticipationRequestDto canselRequest(Integer userId, Integer requestId) {
        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new ValidationException("Запрос на участие не найден"));
        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ValidationException("Can't cansel confirmed request");
        }
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public RatingDto saveRating(Integer userId, Integer eventId, Boolean isLike) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ValidationException("Событие не найдено"));

        boolean alreadyRated = event.getRatings().stream()
                .anyMatch(rating -> rating.getUser().getId().equals(userId));

        if (alreadyRated) {
            throw new ValidationException("Нельзя поставить рейтинг дважды");
        }

        ParticipationRequest request = requestRepository.findByEventIdAndRequesterId(eventId, userId)
                .orElseThrow(() -> new ValidationException("Запрос на участие не найден"));

        if (!request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ValidationException("Нельзя поставить рейтинг, если не участвовать в событии");
        }

        if (isDateCheckEnabled && event.getEventDate().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Нельзя поставить рейтинг событию, которое еще не началось");
        }

        if (Objects.equals(event.getUser().getId(), userId)) {
            throw new ValidationException("Нельзя поставить рейтинг событию, инициатором которого ты являешься");
        }

        Rating rating = new Rating()
                .setEvent(event)
                .setUser(request.getRequester())
                .setIsLike(isLike);

        return ratingMapper.toShow(ratingRepository.save(rating));
    }

    @Override
    public RatingDto updateRating(Integer userId, Integer eventId, Boolean isLike) {
        Rating rating = ratingRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new ValidationException("Рейтинг не найден"));

        if (rating.getIsLike().equals(isLike)) {
            throw new ValidationException("Оценка не изменилась");
        }

        rating.setIsLike(isLike);
        return ratingMapper.toShow(ratingRepository.save(rating));
    }
}
