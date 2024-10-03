package ru.luckyskeet.main.util;

import lombok.AllArgsConstructor;
import ru.luckyskeet.main.dto.EventFullDto;
import ru.luckyskeet.main.util.constants_and_enums.SortCriteria;

import java.util.Comparator;

@AllArgsConstructor
public class EventFullDtoComparator implements Comparator<EventFullDto> {

    private final SortCriteria sortCriteria;

    @Override
    public int compare(EventFullDto event1, EventFullDto event2) {
        return switch (sortCriteria) {
            case EVENT_DATE -> event2.getEventDate().compareTo(event1.getEventDate());
            case VIEWS -> Integer.compare(event2.getViews(), event1.getViews());
            case RATING_DESC -> Integer.compare(calculateRating(event2), calculateRating(event1));
            case RATING_ASC -> Integer.compare(calculateRating(event1), calculateRating(event2));
        };
    }

    private int calculateRating(EventFullDto event) {
        return event.getLikes() - event.getDislikes();
    }
}
