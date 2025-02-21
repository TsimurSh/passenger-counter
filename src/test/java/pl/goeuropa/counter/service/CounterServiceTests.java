package pl.goeuropa.counter.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pl.goeuropa.counter.dto.BusLoadDto;
import pl.goeuropa.counter.repository.PeopleCountRepository;

@SpringBootTest
@ActiveProfiles("test")
public class CounterServiceTests {

    @Autowired
    private CounterService counterService;

    @BeforeEach
    public void init() {
        PeopleCountRepository peopleCountRepository = PeopleCountRepository.getInstance();
        peopleCountRepository.getUpdatesAboutLoads().put("1", new BusLoadDto("1", 0,0,System.currentTimeMillis() - 20000));
        peopleCountRepository.getUpdatesAboutLoads().put("10", new BusLoadDto("1", 0,0,System.currentTimeMillis() - 10000));
        peopleCountRepository.getUpdatesAboutLoads().put("11", new BusLoadDto("1", 0,0,System.currentTimeMillis() - 2000000000));
        peopleCountRepository.getUpdatesAboutLoads().put("12", new BusLoadDto("1", 0,0,System.currentTimeMillis() - 1000000000));
    }

    @Test
    public void shouldReturnNotOlderThen36MinWithTimeObjectTest() {
        var listWithNewestObjects = counterService.getCountDetailsWithTimeCheck();
        Assertions.assertEquals(3, listWithNewestObjects.size());
        Assertions.assertNotNull(listWithNewestObjects.get("time"));
    }
}
