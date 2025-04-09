package user.taskhive.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import my.app.files.controller.LabelController;
import my.app.files.dto.labels.CreateLabelRequestDto;
import my.app.files.dto.labels.LabelDto;
import my.app.files.dto.labels.UpdateLabelRequestDto;
import my.app.files.service.LabelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class LabelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private LabelService labelService;

    @BeforeEach
    void setUp(@Autowired LabelController labelController) {
        labelService = Mockito.mock(LabelService.class);
        ReflectionTestUtils.setField(labelController, "labelService", labelService);
    }

    @Sql(scripts = "classpath:database/label/add-test-label.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/label/clear-db-for-labels.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @WithMockUser(username = "Admin", roles = "USER")
    @Test
    void shouldCreateLabel() throws Exception {
        CreateLabelRequestDto dto = new CreateLabelRequestDto();
        dto.setName("LabelTest");

        LabelDto labelDto = new LabelDto();
        labelDto.setName("LabelTest");
        labelDto.setColor("blue");

        when(labelService.createLabel(any(CreateLabelRequestDto.class))).thenReturn(labelDto);

        mockMvc.perform(post("/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"LabelTest\", \"color\":\"blue\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("LabelTest"))
                .andExpect(jsonPath("$.color").value("blue"));

        verify(labelService, times(1)).createLabel(any(CreateLabelRequestDto.class));
    }

    @WithMockUser(username = "Admin", roles = "USER")
    @Test
    void shouldGetAllLabels() throws Exception {
        LabelDto labelDto1 = new LabelDto();
        labelDto1.setName("Label1");
        labelDto1.setColor("red");

        LabelDto labelDto2 = new LabelDto();
        labelDto2.setName("Label2");
        labelDto2.setColor("green");

        when(labelService.getAllLabels()).thenReturn(Arrays.asList(labelDto1, labelDto2));

        mockMvc.perform(get("/labels")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Label1"))
                .andExpect(jsonPath("$[0].color").value("red"))
                .andExpect(jsonPath("$[1].name").value("Label2"))
                .andExpect(jsonPath("$[1].color").value("green"));

        verify(labelService, times(1)).getAllLabels();
    }

    @WithMockUser(username = "Admin", roles = "USER")
    @Test
    void shouldUpdateLabel() throws Exception {
        UpdateLabelRequestDto updateDto = new UpdateLabelRequestDto();
        updateDto.setName("UpdatedLabel");
        updateDto.setColor("yellow");

        LabelDto updatedLabelDto = new LabelDto();
        updatedLabelDto.setName("UpdatedLabel");
        updatedLabelDto.setColor("yellow");

        when(labelService.updateLabel(eq(1L), any(UpdateLabelRequestDto.class)))
                .thenReturn(updatedLabelDto);

        mockMvc.perform(put("/labels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"UpdatedLabel\", \"color\":\"yellow\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedLabel"))
                .andExpect(jsonPath("$.color").value("yellow"));

        verify(labelService, times(1)).updateLabel(eq(1L),
                any(UpdateLabelRequestDto.class));
    }

    @WithMockUser(username = "Admin", roles = "USER")
    @Test
    void shouldDeleteLabel() throws Exception {
        doNothing().when(labelService).deleteLabel(1L);

        mockMvc.perform(delete("/labels/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(labelService, times(1)).deleteLabel(1L);
    }
}
