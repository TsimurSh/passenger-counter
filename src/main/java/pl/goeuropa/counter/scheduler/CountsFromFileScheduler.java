package pl.goeuropa.counter.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.goeuropa.counter.dto.BusLoadDto;
import pl.goeuropa.counter.dto.LogEntryDto;
import pl.goeuropa.counter.service.ScheduleTasksService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;

@Slf4j
@Component
@ConditionalOnProperty(name = "api.path-to-json-counts-file")
public class CountsFromFileScheduler {

    private final ScheduleTasksService scheduleService;

    @Value("${api.path-to-json-counts-file}")
    String path;

    public CountsFromFileScheduler(ScheduleTasksService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Scheduled(fixedRateString = "${api.interval-to-upload-json-file:15}")
    public void getPeopleCountsFromJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        LogEntryDto newestCount = Files.readAllLines(Paths
                        .get(path)).stream()
                .map(line -> {
                    try {
                        return objectMapper.readValue(line, LogEntryDto.class);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).max(Comparator.comparingLong(LogEntryDto::getTimestamp))
                .orElse(null);

        if (newestCount != null) ;
        log.info("Parsed new object {}", newestCount);
        scheduleService.persistBusLoads(new BusLoadDto(newestCount));
    }
}

