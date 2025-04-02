package user.taskhive.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import my.app.files.dto.labels.CreateLabelRequestDto;
import my.app.files.dto.labels.LabelDto;
import my.app.files.dto.labels.UpdateLabelRequestDto;
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

        label = new Label();
        label.setName("Important");

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
        Label existingLabel = new Label();
        existingLabel.setName("Important");
        when(labelRepository.findByName(dto.getName())).thenReturn(Optional.of(existingLabel));
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            labelService.createLabel(dto);
        });
        assertThat(thrown.getMessage()).isEqualTo("Label with this name already exists");
        verify(labelRepository, times(1)).findByName(dto.getName());
        verify(labelRepository, never()).save(any());
    }

    @Test
    void shouldUpdateLabel() {
        final Long labelId = 1L;
        UpdateLabelRequestDto updateDto = new UpdateLabelRequestDto();
        updateDto.setName("UpdatedName");
        Label labelToUpdate = new Label();
        labelToUpdate.setName("OldName");
        Label updatedLabel = new Label();
        updatedLabel.setName("UpdatedName");
        LabelDto updatedLabelDto = new LabelDto();
        updatedLabelDto.setName("UpdatedName");
        when(labelRepository.findById(labelId)).thenReturn(Optional.of(labelToUpdate));
        when(labelMapper.toDto(updatedLabel)).thenReturn(updatedLabelDto);
        when(labelRepository.save(labelToUpdate)).thenReturn(updatedLabel);
        labelMapper.updateEntity(labelToUpdate, updateDto);
        LabelDto result = labelService.updateLabel(labelId, updateDto);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("UpdatedName");
        verify(labelRepository, times(1)).findById(labelId);
        verify(labelRepository, times(1)).save(labelToUpdate);
    }

    @Test
    void shouldThrowExceptionWhenLabelNotFoundForUpdate() {
        Long labelId = 1L;
        UpdateLabelRequestDto updateDto = new UpdateLabelRequestDto();
        updateDto.setName("UpdatedName");
        when(labelRepository.findById(labelId)).thenReturn(Optional.empty());
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            labelService.updateLabel(labelId, updateDto);
        });
        assertThat(thrown.getMessage()).isEqualTo("Label not found");
        verify(labelRepository, times(1)).findById(labelId);
        verify(labelRepository, never()).save(any());
    }

    @Test
    void shouldDeleteLabel() {
        Long labelId = 1L;
        when(labelRepository.existsById(labelId)).thenReturn(true);
        labelService.deleteLabel(labelId);
        verify(labelRepository, times(1)).deleteById(labelId);
    }

    @Test
    void shouldThrowExceptionWhenLabelNotFoundForDelete() {
        Long labelId = 1L;

        when(labelRepository.existsById(labelId)).thenReturn(false);

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            labelService.deleteLabel(labelId);
        });
        assertThat(thrown.getMessage()).isEqualTo("Label not found");
        verify(labelRepository, times(1)).existsById(labelId);
        verify(labelRepository, never()).deleteById(any());
    }
}

