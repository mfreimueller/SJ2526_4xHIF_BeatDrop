package restfulapi.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import restfulapi.commands.CreatePerformanceCommand;
import restfulapi.dto.PerformanceDto;
import restfulapi.dto.StageInfoDto;
import restfulapi.service.PerformanceService;

@WebMvcTest(PerformanceController.class)
class PerformanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PerformanceService performanceService;

    private final Long sampleId = 1L;

    private final PerformanceDto samplePerformance = PerformanceDto.builder()
            .id(sampleId)
            .artistName("DJ Electric")
            .stageName("Main Stage")
            .day(LocalDate.of(2026, 7, 15))
            .startTime("14:00")
            .durationMinutes(60)
            .build();

    private final StageInfoDto sampleStageInfo = StageInfoDto.builder()
            .stageName("Main Stage")
            .capacity(5000)
            .description("The main outdoor stage")
            .build();

    @Test
    void findAll_shouldReturn200WithList() throws Exception {
        when(performanceService.findAll()).thenReturn(List.of(samplePerformance));

        mockMvc.perform(get("/api/performances"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sampleId))
                .andExpect(jsonPath("$[0].artistName").value("DJ Electric"))
                .andExpect(jsonPath("$[0].stageName").value("Main Stage"))
                .andExpect(jsonPath("$[0].day").value("2026-07-15"))
                .andExpect(jsonPath("$[0].startTime").value("14:00"))
                .andExpect(jsonPath("$[0].durationMinutes").value(60));
    }

    @Test
    void findAll_noPerformances_shouldReturn200WithEmptyList() throws Exception {
        when(performanceService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/performances"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void findById_found_shouldReturn200() throws Exception {
        when(performanceService.findById(sampleId)).thenReturn(Optional.of(samplePerformance));

        mockMvc.perform(get("/api/performances/{id}", sampleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleId));
    }

    @Test
    void findById_notFound_shouldReturn404() throws Exception {
        var unknownId = 999L;
        when(performanceService.findById(unknownId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/performances/{id}", unknownId))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_invalidId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/performances/not-a-number"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPerformance_valid_shouldReturn201() throws Exception {
        when(performanceService.createPerformance(any())).thenReturn(samplePerformance);

        var command = CreatePerformanceCommand.builder()
                .artistName("DJ Electric")
                .stageName("Main Stage")
                .day(LocalDate.of(2026, 7, 15))
                .startTime("14:00")
                .durationMinutes(60)
                .build();

        mockMvc.perform(post("/api/performances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(sampleId));
    }

    @Test
    void createPerformance_invalidInput_shouldReturn400() throws Exception {
        var command = CreatePerformanceCommand.builder()
                .artistName("")
                .stageName("")
                .day(null)
                .startTime("not-a-time")
                .durationMinutes(0)
                .build();

        mockMvc.perform(post("/api/performances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStageInfo_found_shouldReturn200() throws Exception {
        when(performanceService.findStageInfo(sampleId)).thenReturn(Optional.of(sampleStageInfo));

        mockMvc.perform(get("/api/performances/{id}/stage-info", sampleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stageName").value("Main Stage"))
                .andExpect(jsonPath("$.capacity").value(5000))
                .andExpect(jsonPath("$.description").value("The main outdoor stage"));
    }

    @Test
    void getStageInfo_performanceNotFound_shouldReturn404() throws Exception {
        var unknownId = 999L;
        when(performanceService.findStageInfo(unknownId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/performances/{id}/stage-info", unknownId))
                .andExpect(status().isNotFound());
    }
}
