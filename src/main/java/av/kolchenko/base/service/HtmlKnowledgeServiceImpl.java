// src/main/java/av/kolchenko/base/service/HtmlKnowledgeServiceImpl.java
package av.kolchenko.base.service;

import av.kolchenko.base.KnowledgeDtoV1;
import av.kolchenko.base.entity.Knowledge;
import av.kolchenko.base.mapper.KnowledgeMapper;
import av.kolchenko.base.repository.KnowledgeRepository;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class HtmlKnowledgeServiceImpl implements HtmlKnowledgeService {

    private final KnowledgeRepository knowledgeRepository;
    private final KnowledgeMapper knowledgeMapper;
    private final Parser markdownParser;
    private final HtmlRenderer htmlRenderer;

    public HtmlKnowledgeServiceImpl(KnowledgeRepository knowledgeRepository,
                                    KnowledgeMapper knowledgeMapper) {
        this.knowledgeRepository = knowledgeRepository;
        this.knowledgeMapper = knowledgeMapper;

        // Инициализация парсера и рендерера markdown
        MutableDataSet options = new MutableDataSet();
        this.markdownParser = Parser.builder(options).build();
        this.htmlRenderer = HtmlRenderer.builder(options).build();
    }

    @Override
    public KnowledgeDtoV1 getKnowledgeAsHtml(Long id) {
        Knowledge knowledge = knowledgeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Запись с id `%s` не найдена".formatted(id)
                ));

        // Преобразование markdown в HTML
        String questionHtml = convertMarkdownToHtml(knowledge.getQuestion());
        String answerHtml = convertMarkdownToHtml(knowledge.getAnswer());

        // Создание нового DTO с HTML содержимым
        return new KnowledgeDtoV1(
                questionHtml,
                answerHtml,
                knowledge.getBookmark(),
                knowledge.getTopic()
        );
    }

    private String convertMarkdownToHtml(String markdown) {
        if (markdown == null) {
            return "";
        }
        Node document = markdownParser.parse(markdown);
        return htmlRenderer.render(document);
    }
}