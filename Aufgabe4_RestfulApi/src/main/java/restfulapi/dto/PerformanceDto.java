package restfulapi.dto;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record PerformanceDto(
        Long id,
        String artistName,
        String stageName,
        LocalDate day,
        String startTime,
        int durationMinutes
) {}
