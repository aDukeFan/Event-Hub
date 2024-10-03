package ru.luckyskeet.client.stats;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.luckyskeet.dto.stats.EndpointHitDto;
import ru.luckyskeet.dto.stats.ViewStatsDto;

import java.util.List;

@Component
public class StatsClient {

    private final WebClient client;

    public StatsClient(@Value("${stats-server.url}") String statsServerUrl) {
        this.client = WebClient.create(statsServerUrl);
    }

    public void save(EndpointHitDto hitDto) {
        client.post()
                .uri("/hit")
                .body(Mono.just(hitDto), EndpointHitDto.class)
                .retrieve()
                .bodyToMono(EndpointHitDto.class).block();
    }

    public List<ViewStatsDto> get(String app,
                                  String start,
                                  String end,
                                  List<String> uris,
                                  Boolean unique) {
        return client.get()
                .uri(uriBuilder -> uriBuilder.path("/stats")
                        .queryParam("app", app)
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ViewStatsDto>>() {
                })
                .block();
    }
}
