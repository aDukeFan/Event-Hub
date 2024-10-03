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
public class TextSpecification implements Specification<Event> {

    private final String text;

    @Override
    public Predicate toPredicate(@Nonnull Root<Event> root,
                                 @Nonnull CriteriaQuery<?> query,
                                 @Nonnull CriteriaBuilder criteriaBuilder) {
        if (text == null || text.isEmpty()) {
            return criteriaBuilder.conjunction();
        }

        String searchText = "%" + text.toLowerCase() + "%";

        Predicate titlePredicate = criteriaBuilder
                .like(criteriaBuilder.lower(root.get("title")), searchText);
        Predicate annotationPredicate = criteriaBuilder
                .like(criteriaBuilder.lower(root.get("annotation")), searchText);
        Predicate descriptionPredicate = criteriaBuilder
                .like(criteriaBuilder.lower(root.get("description")), searchText);

        return criteriaBuilder.or(titlePredicate, annotationPredicate, descriptionPredicate);
    }
}
