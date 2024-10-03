package ru.luckyskeet.main.mapper;

import org.mapstruct.Mapper;
import ru.luckyskeet.main.dto.CompilationDto;
import ru.luckyskeet.main.model.Compilation;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface CompilationMapper {

    CompilationDto toShow(Compilation compilation);
}
