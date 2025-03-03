package av.kolchenko.base.service;

import av.kolchenko.base.web.dto.KnowledgeDtoV1;
import av.kolchenko.base.entity.Knowledge;
import av.kolchenko.base.entity.TopicType;
import av.kolchenko.base.mapper.KnowledgeMapper;
import av.kolchenko.base.repository.KnowledgeRepository;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class HtmlKnowledgeServiceImpl implements HtmlKnowledgeService {

    private static final Logger logger = LoggerFactory.getLogger(HtmlKnowledgeServiceImpl.class);
    private static final String EXPORT_PATH = "/export/"; // Жёстко задаём путь для экспорта

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
        return new KnowledgeDtoV1(
                knowledge.getId(),
                questionHtml,
                answerHtml,
                knowledge.getBookmark(),
                knowledge.getTopic()
        );
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

        knowledge.setQuestion(dto.getQuestion());
        knowledge.setAnswer(dto.getAnswer());
        knowledge.setBookmark(dto.getBookmark());
        knowledge.setTopic(dto.getTopic());

        Knowledge updatedKnowledge = knowledgeRepository.save(knowledge);

        String questionHtml = convertMarkdownToHtml(updatedKnowledge.getQuestion());
        String answerHtml = convertMarkdownToHtml(updatedKnowledge.getAnswer());
        return new KnowledgeDtoV1(
                updatedKnowledge.getId(),
                questionHtml,
                answerHtml,
                updatedKnowledge.getBookmark(),
                updatedKnowledge.getTopic()
        );
    }

    @Override
    public void exportToMarkdown(Long id, String exportPath) throws IOException {
        KnowledgeDtoV1 knowledge = getKnowledgeRaw(id);
        logger.info("Экспорт записи с ID: {}", id);
        logger.info("Путь для экспорта: {}", EXPORT_PATH); // Теперь используем фиксированный путь

        String safeTopic = knowledge.getTopic() != null ? knowledge.getTopic().toString().replaceAll("[/\\\\:*?\"<>|]", "_") : "NoTopic";
        String rawQuestion = knowledge.getQuestion() != null && !knowledge.getQuestion().isEmpty() ? knowledge.getQuestion() : "NoQuestion";
        String safeQuestion = rawQuestion.replaceAll("^#+\\s*", "").replaceAll("[/\\\\:*?\"<>|]", "_").trim();
        String fileName = safeTopic + "-" + safeQuestion + ".md";
        String filePath = EXPORT_PATH + fileName;

        logger.info("Имя файла: {}", fileName);
        logger.info("Полный путь: {}", filePath);

        StringBuilder markdownContent = new StringBuilder();
        markdownContent.append("Topic: \n## ").append(knowledge.getTopic() != null ? knowledge.getTopic() : "").append("\n\n");
        markdownContent.append("Question: \n").append(knowledge.getQuestion() != null ? knowledge.getQuestion() : "").append("\n\n");
        markdownContent.append("Answer: \n").append(knowledge.getAnswer() != null ? knowledge.getAnswer() : "").append("\n");

        File file = new File(filePath);
        logger.info("Создание директории: {}", file.getParentFile().getAbsolutePath());
        boolean dirsCreated = file.getParentFile().mkdirs();
        if (dirsCreated) {
            logger.info("Директория успешно создана");
        } else {
            logger.info("Директория уже существует или не создана");
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(markdownContent.toString());
            logger.info("Файл успешно создан: {}", filePath);
        } catch (IOException e) {
            logger.error("Ошибка при экспорте в Markdown: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public KnowledgeDtoV1 importFromMarkdown(Long id, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Файл не выбран или пуст");
        }

        logger.info("Импорт данных из файла: {}", file.getOriginalFilename());
        String content = new String(file.getBytes());

        String topic = "";
        String question = "";
        String answer = "";
        String[] lines = content.split("\n");

        int section = 0;
        StringBuilder currentSection = new StringBuilder();
        for (String line : lines) {
            if (line.startsWith("Topic:")) {
                section = 0;
                continue;
            } else if (line.startsWith("Question:")) {
                topic = currentSection.toString().replaceAll("^#+\\s*", "").trim();
                currentSection = new StringBuilder();
                section = 1;
                continue;
            } else if (line.startsWith("Answer:")) {
                question = currentSection.toString().trim();
                currentSection = new StringBuilder();
                section = 2;
                continue;
            }
            currentSection.append(line).append("\n");
        }
        answer = currentSection.toString().trim();

        TopicType topicEnum;
        try {
            topicEnum = topic.isEmpty() ? null : TopicType.valueOf(topic);
        } catch (IllegalArgumentException e) {
            logger.warn("Тема '{}' не соответствует TopicType, используется 'Other'", topic);
            topicEnum = TopicType.Other;
        }

        KnowledgeDtoV1 importedKnowledge = new KnowledgeDtoV1(
                id,
                question,
                answer,
                false,
                topicEnum
        );

        logger.info("Успешно импортированы данные для ID {}: topic={}, question={}, answer={}",
                id, topic, question, answer);
        return importedKnowledge;
    }

    private String convertMarkdownToHtml(String markdown) {
        if (markdown == null) {
            return "";
        }
        Node document = markdownParser.parse(markdown);
        return htmlRenderer.render(document);
    }
}