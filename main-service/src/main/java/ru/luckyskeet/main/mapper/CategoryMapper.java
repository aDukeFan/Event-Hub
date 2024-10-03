package ru.luckyskeet.main.mapper;

import org.mapstruct.Mapper;
import ru.luckyskeet.main.dto.CategoryDto;
import ru.luckyskeet.main.dto.NewCategoryDto;
import ru.luckyskeet.main.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toSave(NewCategoryDto newCategoryDto);

    CategoryDto toShow(Category category);
}
