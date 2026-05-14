package businessservice.persistence;

import java.util.List;
import java.util.Optional;

import businessservice.domain.Stage;

public interface StageRepository {

    Stage save(Stage stage);

    Optional<Stage> findByStageName(String stageName);

    List<Stage> findAll();

    void delete(Stage stage);
}
