package restfulapi.domain;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "identifier")
public class Performance {

    private UUID identifier;
    private String artistName;
    private String stageName;
    private LocalDate day;
    private String startTime;
    private int durationMinutes;
}
