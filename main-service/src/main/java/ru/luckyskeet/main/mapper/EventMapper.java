package ru.luckyskeet.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.luckyskeet.main.dto.EventFullDto;
import ru.luckyskeet.main.dto.EventShortDto;
import ru.luckyskeet.main.dto.NewEventDto;
import ru.luckyskeet.main.model.Event;
import ru.luckyskeet.main.model.Rating;
import ru.luckyskeet.main.util.constants_and_enums.Constants;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class})
public interface EventMapper {

    @Mapping(target = "eventDate", dateFormat = Constants.DATE_TIME_FORMAT)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "participantLimit",
            expression = "java(newEventDto.getParticipantLimit() != null ? newEventDto.getParticipantLimit() : 0)")
    Event toSave(NewEventDto newEventDto);

    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    @Mapping(target = "createdOn", source = "createdOn", dateFormat = Constants.DATE_TIME_FORMAT)
    @Mapping(target = "eventDate", source = "eventDate", dateFormat = Constants.DATE_TIME_FORMAT)
    @Mapping(target = "initiator", source = "user")
    @Mapping(target = "publishedOn", source = "publishedOn", dateFormat = Constants.DATE_TIME_FORMAT)
    @Mapping(target = "views", ignore = true, defaultValue = "0")
    @Mapping(target = "likes", source = "ratings", qualifiedByName = "countLikes")
    @Mapping(target = "dislikes", source = "ratings", qualifiedByName = "countDislikes")
    EventFullDto toShowFull(Event event);

    @Mapping(source = "category", target = "category")
    @Mapping(source = "confirmedRequests", target = "confirmedRequests")
    @Mapping(source = "eventDate", target = "eventDate", dateFormat = Constants.DATE_TIME_FORMAT)
    @Mapping(source = "user", target = "initiator")
    @Mapping(target = "views", ignore = true, defaultValue = "0")
    @Mapping(target = "likes", source = "ratings", qualifiedByName = "countLikes")
    @Mapping(target = "dislikes", source = "ratings", qualifiedByName = "countDislikes")
    EventShortDto toShowShort(Event event);

    @Named("countLikes")
    default Integer countLikes(List<Rating> ratings) {
        return (int) ratings.stream().filter(Rating::getIsLike).count();
    }

    @Named("countDislikes")
    default Integer countDislikes(List<Rating> ratings) {
        return (int) ratings.stream().filter(rating -> !rating.getIsLike()).count();
    }
}
