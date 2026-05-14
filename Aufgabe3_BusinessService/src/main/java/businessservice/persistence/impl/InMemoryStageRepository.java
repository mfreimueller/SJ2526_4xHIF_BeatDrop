package businessservice.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import businessservice.domain.Stage;
import businessservice.persistence.StageRepository;

@Repository
public class InMemoryStageRepository implements StageRepository {

    private final ConcurrentHashMap<String, Stage> store = new ConcurrentHashMap<>();

    @Override
    public Stage save(Stage stage) {
        store.put(stage.getStageName(), stage);
        return stage;
    }

    @Override
    public Optional<Stage> findByStageName(String stageName) {
        return Optional.ofNullable(store.get(stageName));
    }

    @Override
    public List<Stage> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(Stage stage) {
        store.remove(stage.getStageName());
    }
}
