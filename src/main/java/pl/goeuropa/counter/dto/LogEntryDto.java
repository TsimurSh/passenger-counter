package pl.goeuropa.counter.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogEntryDto {

    @JsonProperty("__REALTIME_TIMESTAMP")
    public Long timestamp;

    @JsonProperty("MESSAGE")
    public String message;
}
