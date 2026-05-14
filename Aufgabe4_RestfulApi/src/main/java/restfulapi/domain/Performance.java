package restfulapi.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Performance {

    private Long id;
    private String artistName;
    private String stageName;
    private LocalDate day;
    private String startTime;
    private int durationMinutes;
}
