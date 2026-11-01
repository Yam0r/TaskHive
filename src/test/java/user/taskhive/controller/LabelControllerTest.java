package user.taskhive.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import my.app.files.dto.labels.CreateLabelRequestDto;
import my.app.files.dto.labels.LabelDto;
import my.app.files.dto.labels.UpdateLabelRequestDto;
import my.app.files.exception.EntityNotFoundException;
import my.app.files.exception.LabelAlreadyExistsException;
import my.app.files.service.LabelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LabelService labelService;

    @Test
    @WithMockUser(roles = "USER")
    void createLabel_ValidRequest_ReturnsCreatedLabel() throws Exception {
        when(labelService.createLabel(any(CreateLabelRequestDto.class)))
                .thenReturn(new LabelDto(1L, "LabelTest", "blue"));
        CreateLabelRequestDto requestDto = new CreateLabelRequestDto();
        requestDto.setName("LabelTest");
        requestDto.setColor("blue");

        mockMvc.perform(post("/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("LabelTest"))
                .andExpect(jsonPath("$.color").value("blue"));

        verify(labelService).createLabel(argThat(dto -> "LabelTest".equals(dto.getName())));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createLabel_BlankName_ReturnsBadRequest() throws Exception {
        CreateLabelRequestDto requestDto = new CreateLabelRequestDto();
        requestDto.setName(" ");

        mockMvc.perform(post("/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(labelService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllLabels_ReturnsAllLabels() throws Exception {
        when(labelService.getAllLabels()).thenReturn(List.of(
                new LabelDto(1L, "Label1", "red"),
                new LabelDto(2L, "Label2", "green")));

        mockMvc.perform(get("/labels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Label1"))
                .andExpect(jsonPath("$[1].color").value("green"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateLabel_ValidRequest_ReturnsUpdatedLabel() throws Exception {
        when(labelService.updateLabel(eq(1L), any(UpdateLabelRequestDto.class)))
                .thenReturn(new LabelDto(1L, "UpdatedLabel", "yellow"));
        UpdateLabelRequestDto requestDto = new UpdateLabelRequestDto();
        requestDto.setName("UpdatedLabel");
        requestDto.setColor("yellow");

        mockMvc.perform(put("/labels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedLabel"))
                .andExpect(jsonPath("$.color").value("yellow"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteLabel_ExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/labels/1"))
                .andExpect(status().isNoContent());

        verify(labelService).deleteLabel(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void createLabel_DuplicateName_ReturnsConflict() throws Exception {
        when(labelService.createLabel(any(CreateLabelRequestDto.class)))
                .thenThrow(new LabelAlreadyExistsException("Label with this name already exists"));
        CreateLabelRequestDto requestDto = new CreateLabelRequestDto();
        requestDto.setName("Important");

        mockMvc.perform(post("/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Label with this name already exists"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateLabel_UnknownId_ReturnsNotFound() throws Exception {
        when(labelService.updateLabel(eq(99L), any(UpdateLabelRequestDto.class)))
                .thenThrow(new EntityNotFoundException("Label not found by id: 99"));
        UpdateLabelRequestDto requestDto = new UpdateLabelRequestDto();
        requestDto.setName("New");

        mockMvc.perform(put("/labels/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteLabel_UnknownId_ReturnsNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Label not found by id: 99"))
                .when(labelService).deleteLabel(99L);

        mockMvc.perform(delete("/labels/99"))
                .andExpect(status().isNotFound());
    }
}
