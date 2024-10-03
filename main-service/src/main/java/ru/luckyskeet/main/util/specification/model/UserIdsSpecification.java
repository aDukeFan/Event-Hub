package ru.luckyskeet.main.util.specification.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.luckyskeet.main.model.Event;
import ru.luckyskeet.main.model.User;

import java.util.List;

@AllArgsConstructor
public class UserIdsSpecification implements Specification<Event> {

    private final List<Integer> userIds;

    @Override
    public Predicate toPredicate(@Nonnull Root<Event> root,
                                 @Nonnull CriteriaQuery<?> query,
                                 @Nonnull CriteriaBuilder criteriaBuilder) {
        if (userIds == null || userIds.isEmpty()) {
            return criteriaBuilder.conjunction();
        }
        Join<Event, User> userJoin = root.join("user");
        return userJoin.get("id").in(userIds);
    }
}
