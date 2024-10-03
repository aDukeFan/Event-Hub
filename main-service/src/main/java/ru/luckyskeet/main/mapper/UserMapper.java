package ru.luckyskeet.main.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.luckyskeet.main.dto.UserDto;
import ru.luckyskeet.main.model.Event;
import ru.luckyskeet.main.model.Rating;
import ru.luckyskeet.main.model.User;
import ru.luckyskeet.main.request.NewUserRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toSave(NewUserRequest request);

    @Mapping(target = "rank", source = "events", qualifiedByName = "countRank")
    UserDto toShow(User user);

    @Named("countRank")
    default int countRank(List<Event> events) {
        if (events.isEmpty()) {
            return 0;
        }
        long positiveEventsCount = events.stream().filter(event -> {
            long positiveCount = event.getRatings().stream().filter(Rating::getIsLike).count();
            long negativeCount = event.getRatings().stream().filter(rating -> !rating.getIsLike()).count();
            return positiveCount > negativeCount;
        }).count();
        float rank = (positiveEventsCount / (float) events.size()) * 100;
        return (int) rank;
    }
}
