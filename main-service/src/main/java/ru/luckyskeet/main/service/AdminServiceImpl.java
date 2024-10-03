package ru.luckyskeet.main.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.luckyskeet.main.dto.CategoryDto;
import ru.luckyskeet.main.dto.CompilationDto;
import ru.luckyskeet.main.dto.EventFullDto;
import ru.luckyskeet.main.dto.NewCategoryDto;
import ru.luckyskeet.main.dto.NewCompilationDto;
import ru.luckyskeet.main.dto.UserDto;
import ru.luckyskeet.main.mapper.CategoryMapper;
import ru.luckyskeet.main.mapper.CompilationMapper;
import ru.luckyskeet.main.mapper.EventMapper;
import ru.luckyskeet.main.mapper.UserMapper;
import ru.luckyskeet.main.model.Category;
import ru.luckyskeet.main.model.Compilation;
import ru.luckyskeet.main.model.Event;
import ru.luckyskeet.main.model.User;
import ru.luckyskeet.main.repository.CategoryRepository;
import ru.luckyskeet.main.repository.CompilationRepository;
import ru.luckyskeet.main.repository.EventRepository;
import ru.luckyskeet.main.repository.UserRepository;
import ru.luckyskeet.main.request.NewUserRequest;
import ru.luckyskeet.main.request.UpdateCompilationRequest;
import ru.luckyskeet.main.request.UpdateEventRequest;
import ru.luckyskeet.main.stats.StatsManager;
import ru.luckyskeet.main.util.EventFullDtoComparator;
import ru.luckyskeet.main.util.ServiceUtils;
import ru.luckyskeet.main.util.constants_and_enums.Constants;
import ru.luckyskeet.main.util.constants_and_enums.EventState;
import ru.luckyskeet.main.util.constants_and_enums.SortCriteria;
import ru.luckyskeet.main.util.specification.SpecificationBuilder;
import ru.luckyskeet.main.util.specification.model.CategoryIdsSpecification;
import ru.luckyskeet.main.util.specification.model.StatesSpecification;
import ru.luckyskeet.main.util.specification.model.TimeSelectSpecification;
import ru.luckyskeet.main.util.specification.model.UserIdsSpecification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    private final StatsManager statsManager;
    private final ServiceUtils serviceUtils;

    @Override
    public UserDto saveUser(NewUserRequest newUserRequest) {
        return userMapper.toShow(userRepository.save(userMapper.toSave(newUserRequest)));
    }

    @Override
    public List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size) {
        Pageable pageable = serviceUtils.createPageable(from, size);
        List<User> users = new ArrayList<>();
        if (ids != null) {
            users.addAll(userRepository.findAllByIdIn(ids, pageable));
        } else {
            users.addAll(userRepository.findAll(pageable).toList());
        }

        return users.stream().map(userMapper::toShow).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id "
                    + userId + " does not exist or has already been deleted.");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public CategoryDto saveCategory(NewCategoryDto newCategoryDto) {
        return categoryMapper.toShow(categoryRepository.save(categoryMapper.toSave(newCategoryDto)));
    }

    @Override
    public void deleteCategory(Integer catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new EntityNotFoundException("Compilation with id " + catId
                    + " does not exist or has already been deleted.");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(Integer catId, NewCategoryDto newCategoryDto) {
        Category categoryToUpdate = categoryRepository.findById(catId).orElse(null);
        assert categoryToUpdate != null;
        categoryToUpdate.setName(newCategoryDto.getName());
        return categoryMapper.toShow(categoryRepository.save(categoryToUpdate));
    }

    @Override
    public List<EventFullDto> getEvents(List<Integer> usersIds,
                                        List<String> statesStringList,
                                        List<Integer> categoryIds,
                                        String rangeStart,
                                        String rangeEnd,
                                        String sort,
                                        Integer from,
                                        Integer size) {
        Pageable pageable = serviceUtils.createPageable(from, size);
        LocalDateTime start = serviceUtils.parseLocaleDateTime(rangeStart);
        LocalDateTime end = serviceUtils.parseLocaleDateTime(rangeEnd);
        serviceUtils.validateTimeFrame(start, end);

        List<EventState> stateList = new ArrayList<>();

        if (statesStringList != null && !statesStringList.isEmpty()) {
            statesStringList.forEach(string -> stateList.add(EventState.valueOf(string)));
        }

        Specification<Event> specification = new SpecificationBuilder()
                .withSpecification(new TimeSelectSpecification(start, end))
                .withSpecification(new CategoryIdsSpecification(categoryIds))
                .withSpecification(new UserIdsSpecification(usersIds))
                .withSpecification(new StatesSpecification(stateList))
                .build();

        List<Event> events = eventRepository.findAll(specification, pageable).toList();
        List<EventFullDto> eventFullDtoList = events.stream().map(eventMapper::toShowFull).toList();
        List<EventFullDto> eventFullDtoListWithViews = statsManager
                .addViewsToFullDtosAndGetDtoList(events, eventFullDtoList);
        if (sort == null) {
            return eventFullDtoListWithViews;
        }
        SortCriteria sortCriteria = SortCriteria.valueOf(sort);
        EventFullDtoComparator comparator = new EventFullDtoComparator(sortCriteria);
        return eventFullDtoListWithViews.stream().sorted(comparator).toList();
    }

    public EventFullDto updateEvent(Integer eventId, UpdateEventRequest request) {
        Event eventToUpdate = eventRepository.findById(eventId).orElseThrow();
        if (eventToUpdate.getState() != EventState.PENDING) {
            String message = String.format("Event with ID %d is %s",
                    eventId, eventToUpdate.getState());
            throw new ValidationException(message);
        }

        serviceUtils.updateEventFields(eventToUpdate, request, categoryRepository);

        Optional.ofNullable(request.getStateAction())
                .ifPresent(stateAction -> {
                    if (stateAction.equals(Constants.ADMIN_PUBLISH_EVENT)) {
                        eventToUpdate.setState(EventState.PUBLISHED);
                        eventToUpdate.setPublishedOn(LocalDateTime.now());
                    } else if (stateAction.equals(Constants.ADMIN_REJECT_EVENT)) {
                        eventToUpdate.setState(EventState.REJECTED);
                    }
                });

        Event savedEvent = eventRepository.save(eventToUpdate);
        int views = statsManager.getUniqueEventViews(savedEvent);
        return eventMapper.toShowFull(savedEvent).setViews(views);
    }

    @Override
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(false);
        compilation.setTitle(newCompilationDto.getTitle());
        Optional.ofNullable(newCompilationDto.getEvents()).ifPresent(eventsIds ->
                compilation.setEvents(eventRepository.findAllByIdIn(eventsIds)));
        Optional.ofNullable(newCompilationDto.getPinned()).ifPresent(compilation::setPinned);
        return compilationMapper.toShow(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilationById(Integer compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new EntityNotFoundException("Compilation with id " + compId
                    + " does not exist or has already been deleted.");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest request) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow();
        Optional.ofNullable(request.getTitle()).ifPresent(compilation::setTitle);
        Optional.ofNullable(request.getPinned()).ifPresent(compilation::setPinned);
        Optional.ofNullable(request.getEvents()).ifPresent(events ->
                compilation.setEvents(eventRepository.findAllByIdIn(request.getEvents())));
        return compilationMapper.toShow(compilationRepository.save(compilation));
    }
}
