package av.kolchenko.base.service;

import av.kolchenko.base.KnowledgeDtoV1;
import av.kolchenko.base.KnowledgeFilter;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface KnowledgeService {
    Page<KnowledgeDtoV1> getAll(KnowledgeFilter filter, Pageable pageable);

    KnowledgeDtoV1 getOne(Long id);

    List<KnowledgeDtoV1> getMany(List<Long> ids);

    KnowledgeDtoV1 create(KnowledgeDtoV1 dto);

    KnowledgeDtoV1 patch(Long id, JsonNode patchNode) throws IOException;

    List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException;

    KnowledgeDtoV1 delete(Long id);

    void deleteMany(List<Long> ids);
}
