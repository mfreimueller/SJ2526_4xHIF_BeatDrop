package restfulapi.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import restfulapi.commands.CreateArtistCommand;
import restfulapi.commands.UpdateArtistCommand;
import restfulapi.dto.ArtistDto;
import restfulapi.service.ArtistService;

@WebMvcTest(ArtistController.class)
class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ArtistService artistService;

    private final ArtistDto sampleArtist = ArtistDto.builder()
            .artistName("DJ Electric")
            .genre("Electronic")
            .biography("Award-winning electronic music producer from Vienna")
            .socialMediaHandle("@djelectric")
            .build();

    @Test
    void findAll_shouldReturn200WithList() throws Exception {
        when(artistService.findAll()).thenReturn(List.of(sampleArtist));

        mockMvc.perform(get("/api/artists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].artistName").value("DJ Electric"))
                .andExpect(jsonPath("$[0].genre").value("Electronic"))
                .andExpect(jsonPath("$[0].biography").value("Award-winning electronic music producer from Vienna"))
                .andExpect(jsonPath("$[0].socialMediaHandle").value("@djelectric"));
    }

    @Test
    void findAll_noArtists_shouldReturn200WithEmptyList() throws Exception {
        when(artistService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/artists"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void findByArtistName_found_shouldReturn200() throws Exception {
        when(artistService.findByArtistName("DJ Electric")).thenReturn(Optional.of(sampleArtist));

        mockMvc.perform(get("/api/artists/DJ Electric"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.artistName").value("DJ Electric"));
    }

    @Test
    void findByArtistName_notFound_shouldReturn404() throws Exception {
        when(artistService.findByArtistName("Unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/artists/Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createArtist_valid_shouldReturn201() throws Exception {
        when(artistService.createArtist(any())).thenReturn(sampleArtist);

        var command = CreateArtistCommand.builder()
                .artistName("DJ Electric")
                .genre("Electronic")
                .build();

        mockMvc.perform(post("/api/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.artistName").value("DJ Electric"));
    }

    @Test
    void createArtist_duplicateName_shouldReturn409() throws Exception {
        when(artistService.createArtist(any()))
                .thenThrow(new IllegalArgumentException("Artist with name 'DJ Electric' already exists"));

        var command = CreateArtistCommand.builder()
                .artistName("DJ Electric")
                .genre("Electronic")
                .build();

        mockMvc.perform(post("/api/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict());
    }

    @Test
    void createArtist_invalidInput_shouldReturn400() throws Exception {
        var command = CreateArtistCommand.builder()
                .artistName("")
                .genre("")
                .build();

        mockMvc.perform(post("/api/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateArtist_valid_shouldReturn200() throws Exception {
        var updated = ArtistDto.builder()
                .artistName("DJ Electric")
                .genre("Rock")
                .build();
        when(artistService.updateArtist(eq("DJ Electric"), any())).thenReturn(updated);

        var command = UpdateArtistCommand.builder()
                .genre("Rock")
                .build();

        mockMvc.perform(put("/api/artists/DJ Electric")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genre").value("Rock"));
    }

    @Test
    void updateArtist_notFound_shouldReturn404() throws Exception {
        when(artistService.updateArtist(eq("Unknown"), any()))
                .thenThrow(new IllegalArgumentException("Artist with name 'Unknown' not found"));

        var command = UpdateArtistCommand.builder()
                .genre("Rock")
                .build();

        mockMvc.perform(put("/api/artists/Unknown")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateArtist_invalidInput_shouldReturn400() throws Exception {
        var command = UpdateArtistCommand.builder()
                .genre("")
                .build();

        mockMvc.perform(put("/api/artists/DJ Electric")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }
}
