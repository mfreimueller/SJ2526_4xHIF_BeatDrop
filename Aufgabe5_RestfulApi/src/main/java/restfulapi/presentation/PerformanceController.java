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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import restfulapi.commands.CreatePerformanceCommand;
import restfulapi.dto.PerformanceDto;
import restfulapi.dto.StageInfoDto;
import restfulapi.service.PerformanceService;

@RestController
@RequestMapping("/api/performances")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    @GetMapping
    public ResponseEntity<List<PerformanceDto>> findAll() {
        return ResponseEntity.ok(performanceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceDto> findById(@PathVariable Long id) {
        return performanceService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PerformanceDto> createPerformance(
            @Valid @RequestBody CreatePerformanceCommand command) {
        var performance = performanceService.createPerformance(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(performance);
    }

    @GetMapping("/{id}/stage-info")
    public ResponseEntity<StageInfoDto> getStageInfo(@PathVariable Long id) {
        return performanceService.findStageInfo(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage()));
    }
}
