package businessservice.persistence.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import businessservice.domain.Artist;
import businessservice.domain.Performance;
import businessservice.domain.Stage;
import businessservice.persistence.PerformanceRepository;

@Repository
public class InMemoryPerformanceRepository implements PerformanceRepository {

    private final ConcurrentHashMap<Long, Performance> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Performance save(Performance performance) {
        if (performance.getId() == null) {
            performance.setId(idGenerator.getAndIncrement());
        }
        store.put(performance.getId(), performance);
        return performance;
    }

    @Override
    public Optional<Performance> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Performance> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(Performance performance) {
        store.remove(performance.getId());
    }

    @Override
    public List<Performance> findByStage(Stage stage) {
        return store.values().stream()
                .filter(p -> p.getStage().equals(stage))
                .toList();
    }

    @Override
    public List<Performance> findByArtist(Artist artist) {
        return store.values().stream()
                .filter(p -> p.getArtist().equals(artist))
                .toList();
    }

    @Override
    public List<Performance> findByDay(LocalDate day) {
        return store.values().stream()
                .filter(p -> p.getDay().equals(day))
                .toList();
    }
}
