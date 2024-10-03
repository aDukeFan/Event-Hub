package ru.luckyskeet.main.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.luckyskeet.main.model.Event;
import ru.luckyskeet.main.util.constants_and_enums.EventState;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>, JpaSpecificationExecutor<Event> {

    Boolean existsByIdAndUserId(Integer eventId, Integer userId);

    Event findByIdAndUserId(Integer eventId, Integer userId);

    List<Event> findAllByUserId(Integer userId, Pageable pageable);

    List<Event> findAllByIdIn(List<Integer> ids);

    Optional<Event> findByIdAndState(Integer eventId, EventState state);
}
