package user.taskhive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import my.app.files.dto.labels.CreateLabelRequestDto;
import my.app.files.dto.labels.LabelDto;
import my.app.files.dto.labels.UpdateLabelRequestDto;
import my.app.files.exception.EntityNotFoundException;
import my.app.files.exception.LabelAlreadyExistsException;
import my.app.files.mapper.LabelMapper;
import my.app.files.model.Label;
import my.app.files.repository.LabelRepository;
import my.app.files.service.impl.LabelServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import user.taskhive.config.TestDataUtil;

@ExtendWith(MockitoExtension.class)
class LabelServiceImplTest {

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private LabelMapper labelMapper;

    @InjectMocks
    private LabelServiceImpl labelService;

    @Test
    void createLabel_NewName_SavesAndReturnsLabel() {
        CreateLabelRequestDto requestDto = createRequest("Important");
        when(labelRepository.findByName("Important")).thenReturn(Optional.empty());
        Label label = TestDataUtil.createTestLabel("Important", "red");
        when(labelMapper.toEntity(requestDto)).thenReturn(label);
        when(labelRepository.save(label)).thenReturn(label);
        LabelDto labelDto = new LabelDto(1L, "Important", "red");
        when(labelMapper.toDto(label)).thenReturn(labelDto);

        assertThat(labelService.createLabel(requestDto)).isSameAs(labelDto);
        verify(labelRepository).save(label);
    }

    @Test
    void createLabel_ExistingName_ThrowsAndDoesNotSave() {
        Label existing = TestDataUtil.createTestLabel("Important", "red");
        when(labelRepository.findByName("Important")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> labelService.createLabel(createRequest("Important")))
                .isInstanceOf(LabelAlreadyExistsException.class)
                .hasMessage("Label with this name already exists");
        verify(labelRepository, never()).save(any());
    }

    @Test
    void updateLabel_ExistingId_AppliesChangesAndSaves() {
        Label label = TestDataUtil.createTestLabel("OldName", "blue");
        when(labelRepository.findById(1L)).thenReturn(Optional.of(label));
        when(labelRepository.save(label)).thenReturn(label);
        LabelDto labelDto = new LabelDto(1L, "UpdatedName", "red");
        when(labelMapper.toDto(label)).thenReturn(labelDto);
        UpdateLabelRequestDto requestDto = new UpdateLabelRequestDto();
        requestDto.setName("UpdatedName");

        LabelDto result = labelService.updateLabel(1L, requestDto);

        verify(labelMapper).updateEntity(label, requestDto);
        verify(labelRepository).save(label);
        assertThat(result).isSameAs(labelDto);
    }

    @Test
    void updateLabel_UnknownId_ThrowsAndDoesNotSave() {
        when(labelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> labelService.updateLabel(99L, new UpdateLabelRequestDto()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Label not found by id: 99");
        verify(labelRepository, never()).save(any());
    }

    @Test
    void deleteLabel_ExistingId_DeletesIt() {
        when(labelRepository.existsById(1L)).thenReturn(true);

        labelService.deleteLabel(1L);

        verify(labelRepository).deleteById(1L);
    }

    @Test
    void deleteLabel_UnknownId_ThrowsAndDoesNotDelete() {
        when(labelRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> labelService.deleteLabel(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Label not found by id: 99");
        verify(labelRepository, never()).deleteById(any());
    }

    @Test
    void getAllLabels_MapsEveryLabel() {
        Label first = TestDataUtil.createTestLabel("First", "red");
        Label second = TestDataUtil.createTestLabel("Second", "green");
        when(labelRepository.findAll()).thenReturn(List.of(first, second));
        LabelDto firstDto = new LabelDto(1L, "First", "red");
        when(labelMapper.toDto(first)).thenReturn(firstDto);
        LabelDto secondDto = new LabelDto(2L, "Second", "green");
        when(labelMapper.toDto(second)).thenReturn(secondDto);

        assertThat(labelService.getAllLabels()).containsExactly(firstDto, secondDto);
    }

    private CreateLabelRequestDto createRequest(String name) {
        CreateLabelRequestDto requestDto = new CreateLabelRequestDto();
        requestDto.setName(name);
        return requestDto;
    }
}
