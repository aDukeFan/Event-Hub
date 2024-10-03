package ru.luckyskeet.main.util.specification.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.luckyskeet.main.model.Event;

import java.time.LocalDateTime;

@AllArgsConstructor
public class TimeSelectSpecification implements Specification<Event> {

    private final LocalDateTime start;
    private final LocalDateTime end;

    @Override
    public Predicate toPredicate(@Nonnull Root<Event> root,
                                 @Nonnull CriteriaQuery<?> query,
                                 @Nonnull CriteriaBuilder criteriaBuilder) {
        if (start == null) {
            return criteriaBuilder.conjunction();
        }
        if (end == null) {
            return criteriaBuilder.conjunction();
        }

        return criteriaBuilder.between(root.get("eventDate"), start, end);
    }
}
