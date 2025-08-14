package pl.goeuropa.counter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.goeuropa.counter.dto.BusLoadDto;
import pl.goeuropa.counter.dto.LogEntryDto;
import pl.goeuropa.counter.repository.PeopleCountRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CounterService {

    private final PeopleCountRepository peopleCountRepository = PeopleCountRepository.getInstance();

    @Value("${api.vehicle-name}")
    private String vehicleName;

    @Async
    public void asyncParseJsonFile(List<String> reader) {
        ObjectMapper objectMapper = new ObjectMapper();

        log.debug("Started parsing json file by thread: [{}]", Thread.currentThread().getName());

        LogEntryDto newestCount = null;
        try {
            for (String line : reader) {
                if (line.isBlank()) continue;
                LogEntryDto dto = objectMapper.readValue(line, LogEntryDto.class);
                if (dto.getTimestamp() != null) {
                    if (newestCount == null || dto.getTimestamp() > newestCount.getTimestamp()) {
                        newestCount = dto;
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (newestCount != null) {
            BusLoadDto busLoadDto = new BusLoadDto(newestCount);
            busLoadDto.setVehicleName(vehicleName);
            log.info("Parsed new object {}", busLoadDto);
            peopleCountRepository.getUpdatesAboutLoads()
                    .put(vehicleName, busLoadDto);
            log.debug("{} dto objects is persisted.", peopleCountRepository.getUpdatesAboutLoads().size());
        }
    }

    public Map<String, BusLoadDto> getCountDetailsWithTimeCheck() {
        BusLoadDto timeCheck = new BusLoadDto();
        timeCheck.setVehicleName(LocalDateTime.now().toString());
        timeCheck.setTimestamp(System.currentTimeMillis());
        var busLoadsWithTimeCheck = peopleCountRepository.getUpdatesAboutLoads();
        busLoadsWithTimeCheck.put("time", timeCheck);
        removeOldObjects(peopleCountRepository.getUpdatesAboutLoads());
        return peopleCountRepository.getUpdatesAboutLoads();
    }

    private void removeOldObjects(Map<String, BusLoadDto> objectMap) {
        long currentTime = System.currentTimeMillis();
        long minutesInMillis = 30 * 60 * 1000;

        Iterator<Map.Entry<String, BusLoadDto>> iterator = objectMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, BusLoadDto> entry = iterator.next();
            if ((currentTime - entry.getValue().getTimestamp()) > minutesInMillis ||
                    entry.getValue().getCurrentFullness() > 150) {
                log.debug("Removing old object: " + entry.getValue());
                iterator.remove();
            }
        }
    }
}
