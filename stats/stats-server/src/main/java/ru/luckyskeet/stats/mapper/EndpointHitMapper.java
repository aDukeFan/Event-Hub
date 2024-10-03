package ru.luckyskeet.stats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.luckyskeet.dto.stats.EndpointHitDto;
import ru.luckyskeet.stats.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {

    @Mapping(target = "timestamp",
            source = "timestamp",
            dateFormat = "yyyy-MM-dd HH:mm:ss")
    EndpointHit toSave(EndpointHitDto hitDto);
}
