package user.taskhive.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import my.app.files.dto.attachment.AttachmentDto;
import my.app.files.service.AttachmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@AutoConfigureMockMvc
class AttachmentControllerTest {
    private static final MockMultipartFile FILE = new MockMultipartFile(
            "file", "report.pdf", "application/pdf", "file content".getBytes());

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttachmentService attachmentService;

    @Test
    @WithMockUser(roles = "USER")
    void uploadAttachment_ValidRequest_ReturnsSavedAttachment() throws Exception {
        when(attachmentService.uploadAttachment(eq(1L), any(MultipartFile.class)))
                .thenReturn(createAttachmentDto());

        mockMvc.perform(multipart("/attachments").file(FILE).param("taskId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.filename").value("report.pdf"))
                .andExpect(jsonPath("$.dropboxFileId").value("id:abc"));

        verify(attachmentService).uploadAttachment(eq(1L),
                argThat(file -> "report.pdf".equals(file.getOriginalFilename())));
    }

    @Test
    @WithMockUser(roles = "USER")
    void uploadAttachment_MissingTaskId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(multipart("/attachments").file(FILE))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(attachmentService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void uploadAttachment_UnknownTask_ReturnsNotFound() throws Exception {
        when(attachmentService.uploadAttachment(eq(99L), any(MultipartFile.class)))
                .thenThrow(new EntityNotFoundException("Task not found"));

        mockMvc.perform(multipart("/attachments").file(FILE).param("taskId", "99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAttachments_ReturnsAttachmentsOfTask() throws Exception {
        when(attachmentService.getAttachmentsForTask(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(createAttachmentDto())));

        mockMvc.perform(get("/attachments").param("taskId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].filename").value("report.pdf"));
    }

    @Test
    void uploadAttachment_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(multipart("/attachments").file(FILE).param("taskId", "1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(attachmentService);
    }

    private AttachmentDto createAttachmentDto() {
        return new AttachmentDto(5L, "report.pdf", "id:abc", LocalDateTime.now());
    }
}
