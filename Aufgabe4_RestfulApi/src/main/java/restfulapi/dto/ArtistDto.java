package restfulapi.dto;

import lombok.Builder;

@Builder
public record ArtistDto(
        String artistName,
        String genre,
        String biography,
        String socialMediaHandle
) {}
