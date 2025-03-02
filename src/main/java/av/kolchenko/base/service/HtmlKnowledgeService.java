package av.kolchenko.base.service;

import av.kolchenko.base.KnowledgeDtoV1;

public interface HtmlKnowledgeService {
    KnowledgeDtoV1 getKnowledgeAsHtml(Long id);
    KnowledgeDtoV1 getKnowledgeRaw(Long id);
    KnowledgeDtoV1 updateKnowledge(Long id, KnowledgeDtoV1 dto);
}