package restfulapi.presentation;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import restfulapi.commands.CreateArtistCommand;
import restfulapi.commands.UpdateArtistCommand;
import restfulapi.dto.ArtistDto;
import restfulapi.service.ArtistService;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping
    public ResponseEntity<List<ArtistDto>> findAll() {
        return ResponseEntity.ok(artistService.findAll());
    }

    @GetMapping("/{artistName}")
    public ResponseEntity<ArtistDto> findByArtistName(@PathVariable String artistName) {
        return artistService.findByArtistName(artistName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ArtistDto> createArtist(@Valid @RequestBody CreateArtistCommand command) {
        var artist = artistService.createArtist(command);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{artistName}")
                .buildAndExpand(artist.artistName())
                .toUri();
        return ResponseEntity.created(location).body(artist);
    }

    @PutMapping("/{artistName}")
    public ResponseEntity<ArtistDto> updateArtist(
            @PathVariable String artistName,
            @Valid @RequestBody UpdateArtistCommand command) {
        var artist = artistService.updateArtist(artistName, command);
        return ResponseEntity.ok(artist);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        var message = ex.getMessage();
        if (message != null && message.contains("already exists")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, message));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message));
    }
}
