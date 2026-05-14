package restfulapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import restfulapi.commands.CreateArtistCommand;
import restfulapi.commands.UpdateArtistCommand;
import restfulapi.dto.ArtistDto;

@Service
public class ArtistService {

    public List<ArtistDto> findAll() {
        throw new UnsupportedOperationException("Stub — to be mocked in tests");
    }

    public Optional<ArtistDto> findByArtistName(String artistName) {
        throw new UnsupportedOperationException("Stub — to be mocked in tests");
    }

    public ArtistDto createArtist(CreateArtistCommand command) {
        throw new UnsupportedOperationException("Stub — to be mocked in tests");
    }

    public ArtistDto updateArtist(String artistName, UpdateArtistCommand command) {
        throw new UnsupportedOperationException("Stub — to be mocked in tests");
    }
}
