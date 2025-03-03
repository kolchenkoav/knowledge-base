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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public String getKnowledgeForm(@PathVariable("id") Long id, Model model) {
        KnowledgeDtoV1 knowledge = htmlKnowledgeService.getKnowledgeAsHtml(id);
        model.addAttribute("knowledge", knowledge);
        model.addAttribute("id", id);
        return "knowledge-form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        KnowledgeDtoV1 knowledge = htmlKnowledgeService.getKnowledgeRaw(id);
        model.addAttribute("knowledge", knowledge);
        model.addAttribute("id", id);
        return "knowledge-edit-form";
    }

    @PostMapping("/{id}/edit")
    public String saveEditedKnowledge(@PathVariable("id") Long id,
                                      @ModelAttribute KnowledgeDtoV1 updatedKnowledge,
                                      Model model) {
        htmlKnowledgeService.updateKnowledge(id, updatedKnowledge);
        return "redirect:/api/v2/all";
    }

    @GetMapping("/all")
    public String getAllKnowledge(@ModelAttribute KnowledgeFilter filter,
                                  @PageableDefault(size = 10) Pageable pageable,
                                  @RequestParam(value = "sortField", defaultValue = "id") String sortField,
                                  @RequestParam(value = "sortDirection", defaultValue = "ASC") String sortDirection,
                                  Model model) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<KnowledgeDtoV1> knowledgePage = knowledgeService.getAll(filter, sortedPageable);
        model.addAttribute("knowledgePage", knowledgePage);
        model.addAttribute("filter", filter);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDirection", sortDirection);
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
    public String deleteKnowledge(@PathVariable("id") Long id) { // Исправлено: добавлено имя параметра
        knowledgeService.delete(id);
        return "redirect:/api/v2/all";
    }

    @PostMapping("/{id}/export-md")
    public String exportToMarkdown(@PathVariable("id") Long id, Model model) {
        try {
            htmlKnowledgeService.exportToMarkdown(id, null); // exportPath не используется, но оставляем для совместимости
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

    @PostMapping("/{id}/import-md")
    public String importFromMarkdown(@PathVariable("id") Long id,
                                     @RequestParam("markdownFile") MultipartFile file,
                                     Model model) {
        try {
            KnowledgeDtoV1 importedKnowledge = htmlKnowledgeService.importFromMarkdown(id, file);
            model.addAttribute("knowledge", importedKnowledge);
            model.addAttribute("id", id);
            return "knowledge-edit-form";
        } catch (Exception e) {
            logger.error("Ошибка при импорте из Markdown для ID {}: {}", id, e.getMessage(), e);
            model.addAttribute("error", "Не удалось загрузить файл: " + e.getMessage());
            KnowledgeDtoV1 knowledge = htmlKnowledgeService.getKnowledgeRaw(id);
            model.addAttribute("knowledge", knowledge);
            model.addAttribute("id", id);
            return "knowledge-edit-form";
        }
    }
}