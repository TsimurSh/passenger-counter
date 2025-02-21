package pl.goeuropa.counter.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomeInfoDto {
    private String vehiIdno;
    private int incrPeople;
    private long statisticsTime;
    @JsonProperty("bTimeStr")
    private String timeStr;
}
