package user.taskhive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import user.taskhive.config.TestDataUtil;

@ExtendWith(MockitoExtension.class)
class LabelServiceTest {

    @Mock
    private LabelRepository labelRepository;

    @Mock
    private LabelMapper labelMapper;

    @InjectMocks
    private LabelServiceImpl labelService;

    private CreateLabelRequestDto dto;
    private Label label;
    private LabelDto labelDto;

    @BeforeEach
    void setUp() {
        dto = new CreateLabelRequestDto();
        dto.setName("Important");

        label = TestDataUtil.createTestLabel("Important", "Red");

        labelDto = new LabelDto();
        labelDto.setName("Important");
    }

    @Test
    void shouldCreateLabel() {
        when(labelRepository.findByName(dto.getName())).thenReturn(Optional.empty());
        when(labelMapper.toEntity(dto)).thenReturn(label);
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.toDto(label)).thenReturn(labelDto);

        LabelDto createdLabel = labelService.createLabel(dto);

        assertThat(createdLabel).isNotNull();
        assertThat(createdLabel.getName()).isEqualTo("Important");
        verify(labelRepository, times(1)).findByName(dto.getName());
        verify(labelRepository, times(1)).save(label);
    }

    @Test
    void shouldThrowExceptionWhenLabelAlreadyExists() {
        Label existingLabel = TestDataUtil.createTestLabel("Important", "Red");

        when(labelRepository.findByName(dto.getName())).thenReturn(Optional.of(existingLabel));

        Exception exception = assertThrows(LabelAlreadyExistsException.class, () -> {
            labelService.createLabel(dto);
        });

        assertThat(exception.getMessage()).isEqualTo("Label with this name already exists");
        verify(labelRepository).findByName(dto.getName());
        verify(labelRepository, never()).save(any());
    }

    @Test
    void shouldUpdateLabel() {
        Long labelId = 1L;
        UpdateLabelRequestDto updateDto = new UpdateLabelRequestDto();
        updateDto.setName("UpdatedName");
        updateDto.setColor("Red");

        Label labelToUpdate = TestDataUtil.createTestLabel("OldName", "Blue");

        when(labelRepository.findById(labelId)).thenReturn(Optional.of(labelToUpdate));
        doAnswer(invocation -> {
            labelToUpdate.setName(updateDto.getName());
            labelToUpdate.setColor(updateDto.getColor());
            return null;
        }).when(labelMapper).updateEntity(any(Label.class), any(UpdateLabelRequestDto.class));
        when(labelRepository.save(any(Label.class))).thenReturn(labelToUpdate);
        when(labelMapper.toDto(any(Label.class))).thenReturn(
                LabelDto.builder()
                        .name(updateDto.getName())
                        .color(updateDto.getColor())
                        .build()
        );

        LabelDto result = labelService.updateLabel(labelId, updateDto);

        assertThat(result.getName()).isEqualTo("UpdatedName");
        assertThat(result.getColor()).isEqualTo("Red");

        verify(labelMapper).updateEntity(labelToUpdate, updateDto);
        verify(labelRepository).save(labelToUpdate);
    }

    @Test
    void shouldThrowExceptionWhenLabelNotFoundForUpdate() {
        Long labelId = 1L;
        UpdateLabelRequestDto updateDto = new UpdateLabelRequestDto();
        updateDto.setName("Updated");

        when(labelRepository.findById(labelId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            labelService.updateLabel(labelId, updateDto);
        });

        assertThat(exception.getMessage()).isEqualTo("Label not found by id: 1");
        verify(labelRepository).findById(labelId);
        verify(labelRepository, never()).save(any());
    }

    @Test
    void shouldDeleteLabel() {
        Long labelId = 1L;
        when(labelRepository.existsById(labelId)).thenReturn(true);

        labelService.deleteLabel(labelId);

        verify(labelRepository).deleteById(labelId);
    }

    @Test
    void shouldThrowExceptionWhenLabelNotFoundForDelete() {
        Long labelId = 1L;
        when(labelRepository.existsById(labelId)).thenReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            labelService.deleteLabel(labelId);
        });

        assertThat(exception.getMessage()).isEqualTo("Label not found by id: 1");
        verify(labelRepository).existsById(labelId);
        verify(labelRepository, never()).deleteById(any());
    }
}
