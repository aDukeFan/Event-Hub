package ru.luckyskeet.stats.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.luckyskeet.dto.stats.EndpointHitDto;
import ru.luckyskeet.dto.stats.ViewStatsDto;
import ru.luckyskeet.stats.service.StatsService;

import java.util.List;

@RestController
@AllArgsConstructor
public class StatsController {

    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody EndpointHitDto hitDto) {
        service.save(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> get(@RequestParam(required = false, defaultValue = "ewm-main-service") String app,
                                  @RequestParam String start,
                                  @RequestParam String end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        return service.get(app, start, end, uris, unique);
    }

    // Почему-то согласно спецификации поле app никак не учитывается при get запросах,
    // я поэтому вывел его в отдельный необязательный параметр,
    // который по умолчанию, методу сервиса, передает параметр ewm-main-service для получения статистики,
    // В том числе, чтобы не ломать проверку на gitHub
}
