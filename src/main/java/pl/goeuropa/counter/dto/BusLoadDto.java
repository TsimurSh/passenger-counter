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
        this.vehicleName = parseMessage(logEntry.getMessage(),1);
        this.currentCount = Integer.parseInt(parseMessage(logEntry.getMessage(),2));
        this.currentFullness = Float.NaN;
        this.timestamp = logEntry.getTimestamp() / 1000;
    }

    private String parseMessage(String message, int group) {
        Pattern pattern = Pattern.compile("^\\s*(\\w+)\\s+([0-9]+(?:\\.[0-9]+)?)");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find() && group == 2) return matcher.group(group);
        if (matcher.find() && group == 2) return matcher.group(group);
        return "";
    }
}
