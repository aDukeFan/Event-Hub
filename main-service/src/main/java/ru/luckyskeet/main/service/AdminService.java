package ru.luckyskeet.main.service;

import ru.luckyskeet.main.dto.CategoryDto;
import ru.luckyskeet.main.dto.CompilationDto;
import ru.luckyskeet.main.dto.EventFullDto;
import ru.luckyskeet.main.dto.NewCategoryDto;
import ru.luckyskeet.main.dto.NewCompilationDto;
import ru.luckyskeet.main.dto.UserDto;
import ru.luckyskeet.main.request.NewUserRequest;
import ru.luckyskeet.main.request.UpdateCompilationRequest;
import ru.luckyskeet.main.request.UpdateEventRequest;

import java.util.List;

public interface AdminService {

    UserDto saveUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Integer> ids, Integer from, Integer size);

    void deleteUser(Integer userId);

    CategoryDto saveCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Integer catId);

    CategoryDto updateCategory(Integer catId, NewCategoryDto newCategoryDto);

    List<EventFullDto> getEvents(List<Integer> users,
                                 List<String> states,
                                 List<Integer> categories,
                                 String rangeStart,
                                 String rangeEnd,
                                 String sort,
                                 Integer from,
                                 Integer size);

    EventFullDto updateEvent(Integer eventId, UpdateEventRequest request);

    CompilationDto saveCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Integer compId);

    CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest request);

}
