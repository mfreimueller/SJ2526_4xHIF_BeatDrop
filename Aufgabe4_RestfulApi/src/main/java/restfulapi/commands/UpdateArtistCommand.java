package restfulapi.commands;

import jakarta.validation.constraints.NotBlank;

import lombok.Builder;

@Builder
public record UpdateArtistCommand(
        @NotBlank String genre,
        String biography,
        String socialMediaHandle
) {}