package my.app.files.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import my.app.files.dto.labels.CreateLabelRequestDto;
import my.app.files.dto.labels.LabelDto;
import my.app.files.dto.labels.UpdateLabelRequestDto;
import my.app.files.mapper.LabelMapper;
import my.app.files.model.Label;
import my.app.files.repository.LabelRepository;
import my.app.files.service.LabelService;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public LabelDto createLabel(CreateLabelRequestDto dto) {
        if (labelRepository.findByName(dto.getName()).isPresent()) {
            throw new IllegalArgumentException("Label with this name already exists");
        }
        Label label = labelMapper.toEntity(dto);
        return labelMapper.toDto(labelRepository.save(label));
    }

    @Override
    public List<LabelDto> getAllLabels(){
        return labelRepository.findAll().stream()
                .map(labelMapper::toDto)
                .toList();
    }

    @Override
    public LabelDto updateLabel(Long id, UpdateLabelRequestDto dto){
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Label not found"));
        labelMapper.updateEntity(label, dto);
        return labelMapper.toDto(labelRepository.save(label));
    }

    @Override
    public void deleteLabel(Long id){
        if (!labelRepository.existsById(id)) {
            throw new EntityNotFoundException("Label not found");
        }
        labelRepository.deleteById(id);
    }
}
