package ru.luckyskeet.dto.stats;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class EndpointHitDto {
    String app;
    String uri;
    String ip;
    String timestamp;
}