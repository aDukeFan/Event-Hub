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
public class PaidSpecification implements Specification<Event> {

    private final Boolean paid;

    @Override
    public Predicate toPredicate(@Nonnull Root<Event> root,
                                 @Nonnull CriteriaQuery<?> query,
                                 @Nonnull CriteriaBuilder criteriaBuilder) {
        if (paid == null) {
            return criteriaBuilder.conjunction();
        }
        return root.get("paid").in(paid);
    }
}
