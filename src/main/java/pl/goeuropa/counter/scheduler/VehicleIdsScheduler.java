package pl.goeuropa.counter.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestClient;
import pl.goeuropa.counter.configs.ApiProperties;
import pl.goeuropa.counter.repository.PeopleCountRepository;
import pl.goeuropa.counter.service.ScheduleTasksService;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class VehicleIdsScheduler {

    private final ApiProperties properties;
    private final RestClient restClient;
    private final ScheduleTasksService service;

    private final PeopleCountRepository peopleCountRepository = PeopleCountRepository.getInstance();

    public VehicleIdsScheduler(ApiProperties properties, @Qualifier("tc") RestClient restClient, ScheduleTasksService service) {
        this.properties = properties;
        this.restClient = restClient;
        this.service = service;
    }

    @PostConstruct
    @Scheduled(cron = "@daily")
    public void getVehicleIds() {
        try {
            var response = restClient.get()
                    .uri(properties.getKeyAgency() + "/command/vehiclesDetails?numPreds=3&onlyAssigned=false")
                    .retrieve()
                    .body(String.class);
            assert response != null;
            JSONParser jsonObjectData = new JSONParser(response);

            var vehicleDetails = (List<Map<String, String>>) jsonObjectData.object().get("vehicles");

            service.extractIdsAndPersist(vehicleDetails);

            log.info("Succeed fetched {} vehicleIds from TC : {}",
                    peopleCountRepository.getVehicleIds().size(),
                    peopleCountRepository.getVehicleIds());
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }
}

