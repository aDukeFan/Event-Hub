package ru.luckyskeet.main.util.specification.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.luckyskeet.main.model.Event;

@AllArgsConstructor
public class AvailableSpecification implements Specification<Event> {

    private final Boolean available;

    @Override
    public Predicate toPredicate(@Nonnull Root<Event> root,
                                 @Nonnull CriteriaQuery<?> query,
                                 @Nonnull CriteriaBuilder criteriaBuilder) {
        if (!available) {
            return criteriaBuilder.conjunction();
        }

        Predicate limitGreaterThanOrEqualRequests = criteriaBuilder
                .greaterThanOrEqualTo(root.get("participantLimit"), root.get("confirmedRequests"));

        Predicate limitEqualToZero = criteriaBuilder
                .equal(root.get("participantLimit"), 0);

        return criteriaBuilder.or(limitEqualToZero, limitGreaterThanOrEqualRequests);
    }
}
