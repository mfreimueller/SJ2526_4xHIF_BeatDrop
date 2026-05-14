package restfulapi.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import restfulapi.commands.CreatePerformanceCommand;
import restfulapi.dto.PerformanceDto;
import restfulapi.dto.StageInfoDto;

@Service
public class PerformanceService {

    public List<PerformanceDto> findAll() {
        throw new UnsupportedOperationException("Stub — to be mocked in tests");
    }

    public Optional<PerformanceDto> findById(UUID identifier) {
        throw new UnsupportedOperationException("Stub — to be mocked in tests");
    }

    public PerformanceDto createPerformance(CreatePerformanceCommand command) {
        throw new UnsupportedOperationException("Stub — to be mocked in tests");
    }

    public Optional<StageInfoDto> findStageInfo(UUID performanceId) {
        throw new UnsupportedOperationException("Stub — to be mocked in tests");
    }
}
