package my.app.files.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.labels.CreateLabelRequestDto;
import my.app.files.dto.labels.LabelDto;
import my.app.files.dto.labels.UpdateLabelRequestDto;
import my.app.files.exception.LabelAlreadyExistsException;
import my.app.files.exception.LabelNotFoundException;
import my.app.files.mapper.LabelMapper;
import my.app.files.model.Label;
import my.app.files.repository.LabelRepository;
import my.app.files.service.LabelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                    throw new LabelAlreadyExistsException("Label with this name already exists");
                });

        Label label = labelMapper.toEntity(dto);
        Label savedLabel = labelRepository.save(label);
        return labelMapper.toDto(savedLabel);
    }

    @Override
    public List<LabelDto> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(labelMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public LabelDto updateLabel(Long id, UpdateLabelRequestDto dto) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new LabelNotFoundException("Label not found"));

        labelMapper.updateEntity(label, dto);
        Label updatedLabel = labelRepository.save(label);
        return labelMapper.toDto(updatedLabel);
    }

    @Override
    public void deleteLabel(Long id) {
        if (!labelRepository.existsById(id)) {
            throw new LabelNotFoundException("Label not found");
        }
        labelRepository.deleteById(id);
    }
}
