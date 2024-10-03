package ru.luckyskeet.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.luckyskeet.main.model.ParticipationRequest;
import ru.luckyskeet.main.util.constants_and_enums.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Integer> {
    Optional<ParticipationRequest> findByIdAndRequesterId(Integer requestId, Integer userId);

    List<ParticipationRequest> findByRequesterId(Integer userId);

    Optional<ParticipationRequest> findByEventIdAndRequesterId(Integer eventId, Integer requesterId);

    Boolean existsByEventIdAndRequesterId(Integer eventId, Integer requesterId);

    List<ParticipationRequest> findAllByEventId(Integer eventId);

    List<ParticipationRequest> findAllByEventIdAndStatus(Integer eventId, RequestStatus status);


}
