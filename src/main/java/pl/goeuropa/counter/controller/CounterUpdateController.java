package pl.goeuropa.counter.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.goeuropa.counter.dto.BusLoadDto;
import pl.goeuropa.counter.service.CounterService;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class CounterUpdateController {

    private final CounterService service;

    @GetMapping("/busloads")
    public Map<String, BusLoadDto> getAll() {
        var mapOfCounts = service.getCountDetailsWithTimeCheck();
        log.info("Get {} objects dto with info of occupancy", mapOfCounts.size());
        return mapOfCounts;
    }
}
