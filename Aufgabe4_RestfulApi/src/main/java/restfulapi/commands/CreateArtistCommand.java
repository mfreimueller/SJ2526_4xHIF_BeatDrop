package restfulapi.commands;

import jakarta.validation.constraints.NotBlank;

import lombok.Builder;

@Builder
public record CreateArtistCommand(
        @NotBlank String artistName,
        @NotBlank String genre,
        String biography,
        String socialMediaHandle
) {}
