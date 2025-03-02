package av.kolchenko.base.service;

import av.kolchenko.base.KnowledgeDtoV1;

public interface HtmlKnowledgeService {
    KnowledgeDtoV1 getKnowledgeAsHtml(Long id);
}