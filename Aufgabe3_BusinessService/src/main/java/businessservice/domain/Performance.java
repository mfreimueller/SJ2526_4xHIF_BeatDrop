package businessservice.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private Artist artist;
    private Stage stage;
    private LocalDate day;
    private int startMinute;
    private int durationMinutes;
    private List<TicketOrder> ticketOrders = new ArrayList<>();
}
