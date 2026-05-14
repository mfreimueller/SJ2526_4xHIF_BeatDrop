package restfulapi.dto;

import lombok.Builder;

@Builder
public record StageInfoDto(
        String stageName,
        int capacity,
        String description
) {}
