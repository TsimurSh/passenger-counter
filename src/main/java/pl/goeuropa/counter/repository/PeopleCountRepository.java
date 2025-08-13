package pl.goeuropa.counter.repository;

import lombok.Data;
import pl.goeuropa.counter.dto.BusLoadDto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class PeopleCountRepository {

    private static final PeopleCountRepository singleton = new PeopleCountRepository();

    private final Map<String, BusLoadDto> updatesAboutLoads = new ConcurrentHashMap<>();

    private PeopleCountRepository() {
    }

    public static PeopleCountRepository getInstance() {
        return singleton;
    }

}
