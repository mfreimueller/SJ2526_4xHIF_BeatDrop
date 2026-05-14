package businessservice.persistence;

import java.util.List;
import java.util.Optional;

import businessservice.domain.TicketOrder;

public interface TicketOrderRepository {

    TicketOrder save(TicketOrder ticketOrder);

    Optional<TicketOrder> findById(Long id);

    List<TicketOrder> findAll();

    void delete(TicketOrder ticketOrder);
}
