package ru.luckyskeet.main.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.luckyskeet.main.dto.CategoryDto;
import ru.luckyskeet.main.dto.CompilationDto;
import ru.luckyskeet.main.dto.EventFullDto;
import ru.luckyskeet.main.dto.EventShortDto;
import ru.luckyskeet.main.mapper.CategoryMapper;
import ru.luckyskeet.main.mapper.CompilationMapper;
import ru.luckyskeet.main.mapper.EventMapper;
import ru.luckyskeet.main.model.Category;
import ru.luckyskeet.main.model.Event;
import ru.luckyskeet.main.repository.CategoryRepository;
import ru.luckyskeet.main.repository.CompilationRepository;
import ru.luckyskeet.main.repository.EventRepository;
import ru.luckyskeet.main.stats.StatsManager;
import ru.luckyskeet.main.util.EventShortDtoComparator;
import ru.luckyskeet.main.util.ServiceUtils;
import ru.luckyskeet.main.util.constants_and_enums.EventState;
import ru.luckyskeet.main.util.constants_and_enums.SortCriteria;
import ru.luckyskeet.main.util.specification.SpecificationBuilder;
import ru.luckyskeet.main.util.specification.model.AvailableSpecification;
import ru.luckyskeet.main.util.specification.model.CategoryIdsSpecification;
import ru.luckyskeet.main.util.specification.model.PaidSpecification;
import ru.luckyskeet.main.util.specification.model.TextSpecification;
import ru.luckyskeet.main.util.specification.model.TimeSelectSpecification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PublicServiceImpl implements PublicService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    private final StatsManager statsManager;
    private final ServiceUtils serviceUtils;

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return categoryRepository.findAll(serviceUtils.createPageable(from, size))
                .stream()
                .map(categoryMapper::toShow)
                .toList();
    }

    @Override
    public CategoryDto getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        return categoryMapper.toShow(category);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        return compilationRepository.findAllByPinned(pinned, serviceUtils.createPageable(from, size))
                .stream()
                .map(compilationMapper::toShow)
                .toList();
    }

    @Override
    public CompilationDto getCompilationById(Integer compId) {
        return compilationMapper.toShow(compilationRepository.findById(compId).orElseThrow());
    }

    public List<EventShortDto> getEventsByParams(String text,
                                                 List<Integer> categoryIds,
                                                 Boolean paid,
                                                 String rangeStart,
                                                 String rangeEnd,
                                                 Boolean onlyAvailable,
                                                 String sort,
                                                 Integer from,
                                                 Integer size,
                                                 HttpServletRequest servletRequest) {
        Pageable pageable = serviceUtils.createPageable(from, size);
        LocalDateTime start = serviceUtils.parseLocaleDateTime(rangeStart);
        LocalDateTime end = serviceUtils.parseLocaleDateTime(rangeEnd);
        serviceUtils.validateTimeFrame(start, end);

        Specification<Event> specification = new SpecificationBuilder()
                .withSpecification(new TimeSelectSpecification(start, end))
                .withSpecification(new CategoryIdsSpecification(categoryIds))
                .withSpecification(new PaidSpecification(paid))
                .withSpecification(new AvailableSpecification(onlyAvailable))
                .withSpecification(new TextSpecification(text))
                .build();

        List<Event> events = eventRepository.findAll(specification, pageable).toList();
        List<EventShortDto> eventShortDtoList = events.stream().map(eventMapper::toShowShort).toList();
        statsManager.sendUriView(servletRequest);
        List<EventShortDto> eventShortDtoListWithViews = statsManager
                .addViewsToShortDtosAndGetDtoList(events, eventShortDtoList);
        if (sort == null) {
            return eventShortDtoListWithViews;
        }
        SortCriteria sortCriteria = SortCriteria.valueOf(sort);
        EventShortDtoComparator comparator = new EventShortDtoComparator(sortCriteria);
        return eventShortDtoListWithViews.stream().sorted(comparator).toList();
    }

    @Override
    public EventFullDto getEventById(Integer id, HttpServletRequest servletRequest) {
        Optional<Event> optionalEvent = eventRepository.findByIdAndState(id, EventState.PUBLISHED);
        Event event = optionalEvent.orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
        EventFullDto eventFullDto = eventMapper.toShowFull(event);
        statsManager.sendEventView(event, servletRequest);
        Integer views = statsManager.getUniqueEventViews(event);
        eventFullDto.setViews(views);
        return eventFullDto;
    }
}
