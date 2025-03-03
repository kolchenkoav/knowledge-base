// src/main/java/av/kolchenko/base/service/DatabaseExportImportServiceImpl.java
package av.kolchenko.base.service;

import av.kolchenko.base.web.dto.KnowledgeDtoV1;
import av.kolchenko.base.entity.Knowledge;
import av.kolchenko.base.mapper.KnowledgeMapper;
import av.kolchenko.base.repository.KnowledgeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DatabaseExportImportServiceImpl implements DatabaseExportImportService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseExportImportServiceImpl.class);

    private final KnowledgeRepository knowledgeRepository;
    private final KnowledgeMapper knowledgeMapper;
    private final ObjectMapper objectMapper;

    public DatabaseExportImportServiceImpl(KnowledgeRepository knowledgeRepository,
                                           KnowledgeMapper knowledgeMapper,
                                           ObjectMapper objectMapper) {
        this.knowledgeRepository = knowledgeRepository;
        this.knowledgeMapper = knowledgeMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void exportToJson(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Путь к файлу для экспорта не указан");
        }
        logger.info("Экспорт всех записей в JSON: {}", filePath);
        List<Knowledge> allKnowledge = knowledgeRepository.findAll();
        List<KnowledgeDtoV1> knowledgeDtos = allKnowledge.stream()
                .map(knowledgeMapper::toKnowledgeDtoV1)
                .toList();

        File file = new File(filePath);
        file.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(file)) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, knowledgeDtos);
            logger.info("Успешно экспортировано {} записей в файл: {}", knowledgeDtos.size(), filePath);
        } catch (IOException e) {
            logger.error("Ошибка при экспорте в JSON: {}", e.getMessage(), e);
            throw new IOException("Не удалось записать файл по пути " + filePath + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<KnowledgeDtoV1> importFromJson(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Путь к файлу для импорта не указан");
        }
        logger.info("Импорт записей из JSON: {}", filePath);
        File file = new File(filePath);
        if (!file.exists() || !file.canRead()) {
            throw new IOException("Файл " + filePath + " не существует или недоступен для чтения");
        }

        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        List<KnowledgeDtoV1> importedDtos;

        try {
            importedDtos = objectMapper.readValue(content, new TypeReference<List<KnowledgeDtoV1>>() {});
            logger.info("Успешно импортировано {} записей из файла: {}", importedDtos.size(), filePath);

            // Очищаем ID у импортированных сущностей, чтобы они сохранялись как новые записи
            List<Knowledge> knowledgeEntities = importedDtos.stream()
                    .map(dto -> {
                        Knowledge entity = knowledgeMapper.toEntity(dto);
                        entity.setId(null); // Устанавливаем ID в null для вставки как новой записи
                        return entity;
                    })
                    .toList();
            knowledgeRepository.saveAll(knowledgeEntities);
        } catch (IOException e) {
            logger.error("Ошибка при импорте из JSON: {}", e.getMessage(), e);
            throw new IOException("Не удалось прочитать или обработать файл " + filePath + ": " + e.getMessage(), e);
        }

        return importedDtos;
    }
}