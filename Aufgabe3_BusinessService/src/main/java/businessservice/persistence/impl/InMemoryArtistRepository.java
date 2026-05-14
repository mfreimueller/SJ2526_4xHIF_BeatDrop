package businessservice.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import businessservice.domain.Artist;
import businessservice.persistence.ArtistRepository;

@Repository
public class InMemoryArtistRepository implements ArtistRepository {

    private final ConcurrentHashMap<String, Artist> store = new ConcurrentHashMap<>();

    @Override
    public Artist save(Artist artist) {
        store.put(artist.getArtistName(), artist);
        return artist;
    }

    @Override
    public Optional<Artist> findByArtistName(String artistName) {
        return Optional.ofNullable(store.get(artistName));
    }

    @Override
    public List<Artist> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(Artist artist) {
        store.remove(artist.getArtistName());
    }
}
