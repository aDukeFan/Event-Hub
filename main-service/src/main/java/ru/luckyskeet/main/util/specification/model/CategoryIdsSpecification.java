package ru.luckyskeet.main.util.specification.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.luckyskeet.main.model.Category;
import ru.luckyskeet.main.model.Event;

import java.util.List;

@AllArgsConstructor
public class CategoryIdsSpecification implements Specification<Event> {

    private final List<Integer> catIds;

    @Override
    public Predicate toPredicate(@Nonnull Root<Event> root,
                                 @Nonnull CriteriaQuery<?> query,
                                 @Nonnull CriteriaBuilder criteriaBuilder) {
        if (catIds == null || catIds.isEmpty()) {
            return criteriaBuilder.conjunction();
        }
        Join<Event, Category> categoryJoin = root.join("category");
        return categoryJoin.get("id").in(catIds);
    }

}
