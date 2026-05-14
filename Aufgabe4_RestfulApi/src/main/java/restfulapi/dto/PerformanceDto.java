package restfulapi.dto;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;

@Builder
public record PerformanceDto(
        UUID identifier,
        String artistName,
        String stageName,
        LocalDate day,
        String startTime,
        int durationMinutes
) {}
