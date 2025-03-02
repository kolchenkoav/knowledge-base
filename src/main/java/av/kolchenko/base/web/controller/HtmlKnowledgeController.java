// src/main/java/av/kolchenko/base/web/controller/HtmlKnowledgeController.java
package av.kolchenko.base.web.controller;

import av.kolchenko.base.service.KnowledgeService;
import av.kolchenko.base.web.dto.KnowledgeDtoV1;
import av.kolchenko.base.service.HtmlKnowledgeService;
import av.kolchenko.base.web.filter.KnowledgeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
@RequestMapping("/api/v2")
public class HtmlKnowledgeController {

    private static final Logger logger = LoggerFactory.getLogger(HtmlKnowledgeController.class);

    private final HtmlKnowledgeService htmlKnowledgeService;
    private final KnowledgeService knowledgeService;

    @Value("${parser.output-file-path}")
    private String exportPath;

    public HtmlKnowledgeController(HtmlKnowledgeService htmlKnowledgeService, KnowledgeService knowledgeService) {
        this.htmlKnowledgeService = htmlKnowledgeService;
        this.knowledgeService = knowledgeService;
    }

    @GetMapping("/{id}")
    public String getKnowledgeForm(@PathVariable Long id, Model model) {
        KnowledgeDtoV1 knowledge = htmlKnowledgeService.getKnowledgeAsHtml(id);
        model.addAttribute("knowledge", knowledge);
        model.addAttribute("id", id);
        return "knowledge-form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        KnowledgeDtoV1 knowledge = htmlKnowledgeService.getKnowledgeRaw(id);
        model.addAttribute("knowledge", knowledge);
        model.addAttribute("id", id);
        return "knowledge-edit-form";
    }

    @PostMapping("/{id}/edit")
    public String saveEditedKnowledge(@PathVariable Long id,
                                      @ModelAttribute KnowledgeDtoV1 updatedKnowledge,
                                      Model model) {
        htmlKnowledgeService.updateKnowledge(id, updatedKnowledge);
        return "redirect:/api/v2/all";
    }

    @GetMapping("/all")
    public String getAllKnowledge(@ModelAttribute KnowledgeFilter filter,
                                  @PageableDefault(size = 10) Pageable pageable,
                                  Model model) {
        Page<KnowledgeDtoV1> knowledgePage = knowledgeService.getAll(filter, pageable);
        model.addAttribute("knowledgePage", knowledgePage);
        model.addAttribute("filter", filter);
        return "knowledge-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("knowledge", new KnowledgeDtoV1(0L, "", "", false, null));
        return "knowledge-create-form";
    }

    @PostMapping("/new")
    public String createKnowledge(@ModelAttribute KnowledgeDtoV1 newKnowledge) {
        knowledgeService.create(newKnowledge);
        return "redirect:/api/v2/all";
    }

    @PostMapping("/{id}/delete")
    public String deleteKnowledge(@PathVariable Long id) {
        knowledgeService.delete(id);
        return "redirect:/api/v2/all";
    }

    @PostMapping("/{id}/export-md")
    public String exportToMarkdown(@PathVariable Long id, Model model) {
        try {
            htmlKnowledgeService.exportToMarkdown(id, exportPath);
        } catch (Exception e) {
            logger.error("Ошибка при экспорте в Markdown для ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("error", "Не удалось экспортировать файл: " + e.getMessage());
            KnowledgeDtoV1 knowledge = htmlKnowledgeService.getKnowledgeAsHtml(id);
            model.addAttribute("knowledge", knowledge);
            model.addAttribute("id", id);
            return "knowledge-form";
        }
        return "redirect:/api/v2/" + id;
    }

    // Новый метод для загрузки из Markdown
    @PostMapping("/{id}/import-md")
    public String importFromMarkdown(@PathVariable Long id,
                                     @RequestParam("filePath") String filePath,
                                     Model model) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            logger.info("Загрузка данных из файла: {}", filePath);

            // Парсинг структуры Markdown
            String topic = "";
            String question = "";
            String answer = "";
            String[] lines = content.split("\n");

            int section = 0; // 0 - Topic, 1 - Question, 2 - Answer
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
                    question = currentSection.toString().replaceAll("^#+\\s*", "").trim();
                    currentSection = new StringBuilder();
                    section = 2;
                    continue;
                }
                currentSection.append(line).append("\n");
            }
            answer = currentSection.toString().trim();

            // Создаем DTO с загруженными данными
            KnowledgeDtoV1 importedKnowledge = new KnowledgeDtoV1(
                    id,
                    question,
                    answer,
                    false, // bookmark по умолчанию false
                    topic.isEmpty() ? null : av.kolchenko.base.entity.TopicType.valueOf(topic) // Предполагаем, что topic соответствует enum
            );

            model.addAttribute("knowledge", importedKnowledge);
            model.addAttribute("id", id);
            return "knowledge-edit-form"; // Возвращаем форму с заполненными данными
        } catch (Exception e) {
            logger.error("Ошибка при импорте из Markdown: {}", e.getMessage(), e);
            model.addAttribute("error", "Не удалось загрузить файл: " + e.getMessage());
            KnowledgeDtoV1 knowledge = htmlKnowledgeService.getKnowledgeRaw(id);
            model.addAttribute("knowledge", knowledge);
            model.addAttribute("id", id);
            return "knowledge-edit-form";
        }
    }
}