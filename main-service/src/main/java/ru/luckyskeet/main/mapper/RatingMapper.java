package ru.luckyskeet.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.luckyskeet.main.dto.RatingDto;
import ru.luckyskeet.main.model.Rating;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "userId", source = "user.id")
    RatingDto toShow(Rating rating);
}
