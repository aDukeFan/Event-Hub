package ru.luckyskeet.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.luckyskeet.main.model.Rating;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    Boolean existsByUserIdAndEventId(Integer userId, Integer eventId);

    Optional<Rating> findByUserIdAndEventId(Integer userId, Integer eventId);
}
