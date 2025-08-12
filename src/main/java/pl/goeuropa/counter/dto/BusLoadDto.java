package pl.goeuropa.counter.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
public class BusLoadDto implements Serializable {

    private String vehicleName;
    private int currentCount;
    private float currentFullness;
    private long timestamp;

    public BusLoadDto(LogEntryDto logEntry) {
        this.vehicleName = "Unknown";
        this.currentCount = getLoadAsInt(logEntry.getMessage());
        this.currentFullness = Float.NaN;
        this.timestamp = logEntry.getTimestamp() / 1000;
    }

    private int getLoadAsInt(String message) {
        Pattern pattern = Pattern.compile("roomname\\s+(\\d+)\\.");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) return Integer.parseInt(matcher.group(1));
        return -1;
    }
}
