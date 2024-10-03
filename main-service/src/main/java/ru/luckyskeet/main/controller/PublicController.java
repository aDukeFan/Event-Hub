package ru.luckyskeet.main.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.luckyskeet.main.dto.CategoryDto;
import ru.luckyskeet.main.dto.CompilationDto;
import ru.luckyskeet.main.dto.EventFullDto;
import ru.luckyskeet.main.dto.EventShortDto;
import ru.luckyskeet.main.service.PublicService;

import java.util.List;

@RestController
@RequestMapping
@AllArgsConstructor
public class PublicController {

    private final PublicService service;

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0", required = false) Integer from,
                                           @RequestParam(defaultValue = "10", required = false) Integer size) {
        return service.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategoryById(@PathVariable Integer catId) {
        return service.getCategoryById(catId);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam(defaultValue = "false", required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0", required = false) Integer from,
                                                @RequestParam(defaultValue = "10", required = false) Integer size) {
        return service.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationById(@PathVariable Integer compId) {
        return service.getCompilationById(compId);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEventsByParams(@RequestParam(defaultValue = "", required = false) String text,
                                                 @RequestParam(required = false) List<Integer> categories,
                                                 @RequestParam(required = false) Boolean paid,
                                                 @RequestParam(required = false) String rangeStart,
                                                 @RequestParam(required = false) String rangeEnd,
                                                 @RequestParam(defaultValue = "false", required = false) Boolean onlyAvailable,
                                                 @RequestParam(required = false) String sort,
                                                 @RequestParam(defaultValue = "0", required = false) Integer from,
                                                 @RequestParam(defaultValue = "10", required = false) Integer size,
                                                 HttpServletRequest servletRequest) {
        return service.getEventsByParams(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size, servletRequest);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventById(@PathVariable Integer id, HttpServletRequest servletRequest) {
        return service.getEventById(id, servletRequest);
    }
}
