package av.kolchenko.base.service;

import av.kolchenko.base.web.dto.KnowledgeDtoV1;
import av.kolchenko.base.web.filter.KnowledgeFilter;
import av.kolchenko.base.entity.Knowledge;
import av.kolchenko.base.mapper.KnowledgeMapper;
import av.kolchenko.base.repository.KnowledgeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    private final KnowledgeMapper knowledgeMapper;

    private final KnowledgeRepository knowledgeRepository;

    private final ObjectMapper objectMapper;

    public KnowledgeServiceImpl(KnowledgeMapper knowledgeMapper,
                                KnowledgeRepository knowledgeRepository,
                                ObjectMapper objectMapper) {
        this.knowledgeMapper = knowledgeMapper;
        this.knowledgeRepository = knowledgeRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Page<KnowledgeDtoV1> getAll(KnowledgeFilter filter, Pageable pageable) {
        Specification<Knowledge> spec = filter.toSpecification();
        Page<Knowledge> knowledges = knowledgeRepository.findAll(spec, pageable);
        return knowledges.map(knowledgeMapper::toKnowledgeDtoV1);
    }

//    @Override
//    public Page<KnowledgeDtoV1> getAll(KnowledgeFilter filter, Pageable pageable) {
//        Specification<Knowledge> spec = filter.toSpecification();
//        Page<Knowledge> knowledges = knowledgeRepository.findAll(spec, pageable);
//        return knowledges.map(knowledgeMapper::toKnowledgeDtoV1);
//    }

    @Override
    public KnowledgeDtoV1 getOne(Long id) {
        Optional<Knowledge> knowledgeOptional = knowledgeRepository.findById(id);
        return knowledgeMapper.toKnowledgeDtoV1(knowledgeOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @Override
    public List<KnowledgeDtoV1> getMany(List<Long> ids) {
        List<Knowledge> knowledges = knowledgeRepository.findAllById(ids);
        return knowledges.stream()
                .map(knowledgeMapper::toKnowledgeDtoV1)
                .toList();
    }

    @Override
    public KnowledgeDtoV1 create(KnowledgeDtoV1 dto) {
        Knowledge knowledge = knowledgeMapper.toEntity(dto);
        Knowledge resultKnowledge = knowledgeRepository.save(knowledge);
        return knowledgeMapper.toKnowledgeDtoV1(resultKnowledge);
    }

    @Override
    public KnowledgeDtoV1 patch(Long id, JsonNode patchNode) throws IOException {
        Knowledge knowledge = knowledgeRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        KnowledgeDtoV1 knowledgeDtoV1 = knowledgeMapper.toKnowledgeDtoV1(knowledge);
        objectMapper.readerForUpdating(knowledgeDtoV1).readValue(patchNode);
        knowledgeMapper.updateWithNull(knowledgeDtoV1, knowledge);

        Knowledge resultKnowledge = knowledgeRepository.save(knowledge);
        return knowledgeMapper.toKnowledgeDtoV1(resultKnowledge);
    }

    @Override
    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<Knowledge> knowledges = knowledgeRepository.findAllById(ids);

        for (Knowledge knowledge : knowledges) {
            KnowledgeDtoV1 knowledgeDtoV1 = knowledgeMapper.toKnowledgeDtoV1(knowledge);
            objectMapper.readerForUpdating(knowledgeDtoV1).readValue(patchNode);
            knowledgeMapper.updateWithNull(knowledgeDtoV1, knowledge);
        }

        List<Knowledge> resultKnowledges = knowledgeRepository.saveAll(knowledges);
        return resultKnowledges.stream()
                .map(Knowledge::getId)
                .toList();
    }

    @Override
    public KnowledgeDtoV1 delete(Long id) {
        Knowledge knowledge = knowledgeRepository.findById(id).orElse(null);
        if (knowledge != null) {
            knowledgeRepository.delete(knowledge);
        }
        return knowledgeMapper.toKnowledgeDtoV1(knowledge);
    }

    @Override
    public void deleteMany(List<Long> ids) {
        knowledgeRepository.deleteAllById(ids);
    }
}
