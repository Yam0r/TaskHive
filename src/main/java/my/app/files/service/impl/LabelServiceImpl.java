package my.app.files.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.app.files.dto.labels.CreateLabelRequestDto;
import my.app.files.dto.labels.LabelDto;
import my.app.files.dto.labels.UpdateLabelRequestDto;
import my.app.files.exception.EntityNotFoundException;
import my.app.files.exception.LabelAlreadyExistsException;
import my.app.files.mapper.LabelMapper;
import my.app.files.model.Label;
import my.app.files.repository.LabelRepository;
import my.app.files.service.LabelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public LabelDto createLabel(CreateLabelRequestDto dto) {
        labelRepository.findByName(dto.getName())
                .ifPresent(label -> {
                    log.warn("Attempt to create label with existing name: {}", dto.getName());
                    throw new LabelAlreadyExistsException("Label with this name already exists");
                });

        Label label = labelMapper.toEntity(dto);
        Label savedLabel = labelRepository.save(label);
        log.info("Created new label with id: {}", savedLabel.getId());
        return labelMapper.toDto(savedLabel);
    }

    @Override
    public LabelDto updateLabel(Long id, UpdateLabelRequestDto dto) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Label not found for update, id: {}", id);
                    return new EntityNotFoundException("Label not found by id: " + id);
                });

        labelMapper.updateEntity(label, dto);
        Label updatedLabel = labelRepository.save(label);
        log.info("Updated label with id: {}", updatedLabel.getId());
        return labelMapper.toDto(updatedLabel);
    }

    @Override
    public void deleteLabel(Long id) {
        if (!labelRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent label with id: {}", id);
            throw new EntityNotFoundException("Label not found by id: " + id);
        }
        labelRepository.deleteById(id);
        log.info("Deleted label with id: {}", id);
    }

    @Override
    public List<LabelDto> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(labelMapper::toDto)
                .collect(Collectors.toList());
    }
}
