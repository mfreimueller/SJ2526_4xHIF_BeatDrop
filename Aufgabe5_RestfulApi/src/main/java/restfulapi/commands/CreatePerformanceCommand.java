package restfulapi.commands;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.Builder;

@Builder
public record CreatePerformanceCommand(
        @NotBlank String artistName,
        @NotBlank String stageName,
        @NotNull LocalDate day,
        @NotBlank @Pattern(regexp = "\\d{2}:\\d{2}") String startTime,
        @Min(1) int durationMinutes
) {}
