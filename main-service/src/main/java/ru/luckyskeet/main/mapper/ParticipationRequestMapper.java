package ru.luckyskeet.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.luckyskeet.main.dto.ParticipationRequestDto;
import ru.luckyskeet.main.model.ParticipationRequest;
import ru.luckyskeet.main.util.constants_and_enums.Constants;

@Mapper(componentModel = "spring")
public interface ParticipationRequestMapper {

    @Mapping(source = "created", target = "created", dateFormat = Constants.DATE_TIME_FORMAT)
    @Mapping(source = "event.id", target = "event")
    @Mapping(source = "requester.id", target = "requester")
    @Mapping(source = "status", target = "status")
    ParticipationRequestDto toDto(ParticipationRequest participationRequest);
}
