package ru.luckyskeet.main.util.specification.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.luckyskeet.main.model.Event;
import ru.luckyskeet.main.util.constants_and_enums.EventState;

import java.util.List;

@AllArgsConstructor
public class StatesSpecification implements Specification<Event> {

    private final List<EventState> states;

    @Override
    public Predicate toPredicate(@Nonnull Root<Event> root,
                                 @Nonnull CriteriaQuery<?> query,
                                 @Nonnull CriteriaBuilder criteriaBuilder) {
        if (states == null || states.isEmpty()) {
            return criteriaBuilder.conjunction();
        }
        return root.get("state").in(states);
    }
}
