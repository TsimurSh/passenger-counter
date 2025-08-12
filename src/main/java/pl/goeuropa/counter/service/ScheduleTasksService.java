package pl.goeuropa.counter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import pl.goeuropa.counter.dto.BusLoadDto;
import pl.goeuropa.counter.repository.PeopleCountRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "api.path-to-json-counts-file")
public class ScheduleTasksService {

    private final PeopleCountRepository peopleCountRepository = PeopleCountRepository.getInstance();

    public void persistBusLoads(BusLoadDto busLoads) {
        peopleCountRepository.getUpdatesAboutLoads().put("Unknown", busLoads);
        log.debug("{} dto objects is persisted.", peopleCountRepository.getUpdatesAboutLoads().size());
    }
}
