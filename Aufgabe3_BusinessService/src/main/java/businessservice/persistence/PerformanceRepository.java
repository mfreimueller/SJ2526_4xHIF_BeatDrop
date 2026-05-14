package businessservice.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import businessservice.domain.Artist;
import businessservice.domain.Performance;
import businessservice.domain.Stage;

public interface PerformanceRepository {

    Performance save(Performance performance);

    Optional<Performance> findById(Long id);

    List<Performance> findAll();

    void delete(Performance performance);

    List<Performance> findByStage(Stage stage);

    List<Performance> findByArtist(Artist artist);

    List<Performance> findByDay(LocalDate day);
}
