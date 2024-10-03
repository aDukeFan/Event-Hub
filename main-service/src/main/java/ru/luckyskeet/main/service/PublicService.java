package ru.luckyskeet.main.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.luckyskeet.main.dto.CategoryDto;
import ru.luckyskeet.main.dto.CompilationDto;
import ru.luckyskeet.main.dto.EventFullDto;
import ru.luckyskeet.main.dto.EventShortDto;

import java.util.List;

public interface PublicService {

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Integer id);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Integer compId);

    List<EventShortDto> getEventsByParams(String text,
                                          List<Integer> categories,
                                          Boolean paid,
                                          String rangeStart,
                                          String rangeEnd,
                                          Boolean onlyAvailable,
                                          String sort,
                                          Integer from,
                                          Integer size,
                                          HttpServletRequest servletRequest);

    EventFullDto getEventById(Integer id, HttpServletRequest servletRequest);

}
