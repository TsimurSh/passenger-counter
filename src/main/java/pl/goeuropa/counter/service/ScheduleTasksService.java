package pl.goeuropa.counter.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.goeuropa.counter.dto.BusLoadDto;
import pl.goeuropa.counter.repository.PeopleCountRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleTasksService {

    private final PeopleCountRepository peopleCountRepository = PeopleCountRepository.getInstance();

    public void extractIdsAndPersist(List<Map<String, String>> vehicleDetails) {
        peopleCountRepository.getVehicleIds().addAll(vehicleDetails.stream()
                .map(vehicle -> vehicle.get("vehicleName")
                        .startsWith("ME")
                        ? vehicle.get("vehicleName").concat("-APC2")
                        : vehicle.get("vehicleName").concat("-APC"))
                .collect(Collectors.toSet()));
        writeIdsAsValidString();
    }

    public void setJSessionId(@NonNull Map response) {
        peopleCountRepository.setJSessionId(
                (String) response.get("JSESSIONID"));
    }

    private void writeIdsAsValidString() {
        peopleCountRepository.getVehicleIds().remove("ME-8-APC2");
        peopleCountRepository.setVehiIdnosParam(
                String.join(",",
                        peopleCountRepository.getVehicleIds()));
    }

    public void persistBusLoads(@NonNull List<BusLoadDto> loads) {
        peopleCountRepository.getUpdatesAboutLoads().putAll(
                loads.stream()
                        .collect(Collectors.toConcurrentMap(
                                BusLoadDto::getVehicleName,
                                dto -> dto,
                                (dto1, dto2) -> {
                                    return dto1.getTimestamp() > dto2.getTimestamp() ? dto1 : dto2;
                                }
                        ))
        );
        log.debug("{} dto objects is persisted.", peopleCountRepository.getUpdatesAboutLoads().size());
    }
}
