package ru.luckyskeet.main.util;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.luckyskeet.main.exception.BadTimFrameException;
import ru.luckyskeet.main.model.Category;
import ru.luckyskeet.main.model.Event;
import ru.luckyskeet.main.model.Location;
import ru.luckyskeet.main.repository.CategoryRepository;
import ru.luckyskeet.main.request.UpdateEventRequest;
import ru.luckyskeet.main.service.LocationService;
import ru.luckyskeet.main.util.constants_and_enums.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ServiceUtils {

    private final LocationService locationService;

    public Pageable createPageable(Integer from, Integer size) {
        return PageRequest.of(from / size, size);
    }

    public LocalDateTime parseLocaleDateTime(String stringDate) {
        if (stringDate == null || stringDate.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(stringDate,
                DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT));
    }

    public void validateTimeFrame(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new BadTimFrameException("start must be after end");
        }
    }

    public void updateEventFields(Event eventToUpdate,
                                  UpdateEventRequest request,
                                  CategoryRepository categoryRepository) {
        Optional.ofNullable(request.getTitle()).ifPresent(eventToUpdate::setTitle);
        Optional.ofNullable(request.getDescription()).ifPresent(eventToUpdate::setDescription);
        Optional.ofNullable(request.getAnnotation()).ifPresent(eventToUpdate::setAnnotation);
        Optional.ofNullable(request.getPaid()).ifPresent(eventToUpdate::setPaid);
        Optional.ofNullable(request.getParticipantLimit()).ifPresent(eventToUpdate::setParticipantLimit);

        Optional.ofNullable(request.getCategory())
                .ifPresent(newCategoryId -> {
                    Category category = categoryRepository.findById(newCategoryId)
                            .orElseThrow(() -> new EntityNotFoundException("Category not found"));
                    eventToUpdate.setCategory(category);
                });

        Optional.ofNullable(request.getEventDate())
                .ifPresent(newEventDate -> eventToUpdate
                        .setEventDate(parseLocaleDateTime(newEventDate)));

        Optional.ofNullable(request.getLocation())
                .ifPresent(newLocationDto -> {
                    Location location = locationService
                            .saveOrGetLocation(newLocationDto.getLon(), newLocationDto.getLat());
                    eventToUpdate.setLocation(location);
                });
    }

}
