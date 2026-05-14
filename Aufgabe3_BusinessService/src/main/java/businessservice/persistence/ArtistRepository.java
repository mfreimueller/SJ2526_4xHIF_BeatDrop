package businessservice.persistence;

import java.util.List;
import java.util.Optional;

import businessservice.domain.Artist;

public interface ArtistRepository {

    Artist save(Artist artist);

    Optional<Artist> findByArtistName(String artistName);

    List<Artist> findAll();

    void delete(Artist artist);
}
