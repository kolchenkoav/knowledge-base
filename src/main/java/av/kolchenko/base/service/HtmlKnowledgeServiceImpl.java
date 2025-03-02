package av.kolchenko.base.service;

import av.kolchenko.base.web.dto.KnowledgeDtoV1;
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

        String questionHtml = convertMarkdownToHtml(knowledge.getQuestion());
        String answerHtml = convertMarkdownToHtml(knowledge.getAnswer());
        KnowledgeDtoV1 dto = new KnowledgeDtoV1(
                knowledge.getId(),
                questionHtml,
                answerHtml,
                knowledge.getBookmark(),
                knowledge.getTopic()
        );
        dto.setShortAnswer(convertMarkdownToHtml(truncateAnswer(knowledge.getAnswer())));
        return dto;
    }

    @Override
    public KnowledgeDtoV1 getKnowledgeRaw(Long id) {
        Knowledge knowledge = knowledgeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Запись с id `%s` не найдена".formatted(id)
                ));
        return knowledgeMapper.toKnowledgeDtoV1(knowledge);
    }

    @Override
    public KnowledgeDtoV1 updateKnowledge(Long id, KnowledgeDtoV1 dto) {
        Knowledge knowledge = knowledgeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Запись с id `%s` не найдена".formatted(id)
                ));

        // Обновляем поля сущности
        knowledge.setQuestion(dto.getQuestion());
        knowledge.setAnswer(dto.getAnswer());
        knowledge.setBookmark(dto.getBookmark());
        knowledge.setTopic(dto.getTopic());

        // Сохраняем изменения
        Knowledge updatedKnowledge = knowledgeRepository.save(knowledge);

        // Преобразуем markdown в HTML для возврата
        String questionHtml = convertMarkdownToHtml(updatedKnowledge.getQuestion());
        String answerHtml = convertMarkdownToHtml(updatedKnowledge.getAnswer());
        String shortAnswerHtml = convertMarkdownToHtml(truncateAnswer(updatedKnowledge.getAnswer()));

        KnowledgeDtoV1 resultDto = new KnowledgeDtoV1(
                updatedKnowledge.getId(),
                questionHtml,
                answerHtml,
                updatedKnowledge.getBookmark(),
                updatedKnowledge.getTopic()
        );
        resultDto.setShortAnswer(shortAnswerHtml); // Устанавливаем shortAnswer
        return resultDto;
    }

    private String convertMarkdownToHtml(String markdown) {
        if (markdown == null) {
            return "";
        }
        Node document = markdownParser.parse(markdown);
        return htmlRenderer.render(document);
    }

    private String truncateAnswer(String fullAnswer) {
        if (fullAnswer == null || fullAnswer.length() <= 100) {
            return fullAnswer;
        }
        return fullAnswer.substring(0, 100) + "...";
    }
}