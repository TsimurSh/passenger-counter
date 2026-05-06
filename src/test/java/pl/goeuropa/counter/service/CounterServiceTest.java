package pl.goeuropa.counter.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CounterServiceTest {

    @Autowired
    private CounterService counterService;

    @Test
    void asyncParseJsonFile() {
        counterService.asyncParseJsonFile(List.of());
    }
}
