package businessservice.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import businessservice.domain.TicketOrder;
import businessservice.persistence.TicketOrderRepository;

@Repository
public class InMemoryTicketOrderRepository implements TicketOrderRepository {

    private final ConcurrentHashMap<Long, TicketOrder> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public TicketOrder save(TicketOrder ticketOrder) {
        if (ticketOrder.getId() == null) {
            ticketOrder.setId(idGenerator.getAndIncrement());
        }
        store.put(ticketOrder.getId(), ticketOrder);
        return ticketOrder;
    }

    @Override
    public Optional<TicketOrder> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<TicketOrder> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(TicketOrder ticketOrder) {
        store.remove(ticketOrder.getId());
    }
}
