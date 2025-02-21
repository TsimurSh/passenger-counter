package pl.goeuropa.counter.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pl.goeuropa.counter.dto.BusLoadDto;
import pl.goeuropa.counter.dto.IncomeInfoDto;
import pl.goeuropa.counter.repository.PeopleCountRepository;
import pl.goeuropa.counter.service.ScheduleTasksService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class PeopleDetailScheduler {

    private final RestClient restClient;
    private final ScheduleTasksService service;
    private final JsessionScheduler session;

    private final PeopleCountRepository peopleCountRepository = PeopleCountRepository
            .getInstance();
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PeopleDetailScheduler(@Qualifier("video") RestClient restClient, ScheduleTasksService service, JsessionScheduler session) {
        this.restClient = restClient;
        this.service = service;
        this.session = session;
    }

    @Scheduled(fixedRate = 15000)
    public void getBusloads() {
        LocalDateTime now = LocalDateTime.now();
        String time = now.minusSeconds(15).format(timeFormatter);
        String date = now.format(dateFormatter);
        try {
            var jsessionid = peopleCountRepository.getJSessionId();
            var vehiIdnos = peopleCountRepository.getVehiIdnosParam();
            var response = restClient.get()
                    .uri("PeopleAction_peopleDetail.action?" +
                            "jsession={jsessionid}&begintime={date} {time}&endtime={date} 23:59:59&" +
                            "vehiIdnos={vehiIdnos}&pageRecords=10000&currentPage=1", jsessionid, date, time, date, vehiIdnos)
                    .retrieve()
                    .body(Map.class);

            assert response != null;
            if (response.size() == 2 && response.get("result").equals(5)) {
                session.getJSessionId();
                log.info("Jsession is expired or {}. Try the new one.", response.get("message"));
            } else service.persistBusLoads(getLoadDetailsDto(response));
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    private List<BusLoadDto> getLoadDetailsDto(@NonNull Map response) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<?> infos = (List<?>) response.get("infos");
        log.debug("Get {} newer historical records from remote server.", infos.size());
        return objectMapper.convertValue(infos, new TypeReference<List<IncomeInfoDto>>() {
                })
                .stream()
                .map(BusLoadDto::new)
                .filter(object -> object.getCurrentCount() >= 0)
                .toList();
    }
}
