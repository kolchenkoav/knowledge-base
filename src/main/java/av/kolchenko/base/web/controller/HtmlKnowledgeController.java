// src/main/java/av/kolchenko/base/controller/HtmlKnowledgeController.java
package av.kolchenko.base.web.controller;

import av.kolchenko.base.service.KnowledgeService;
import av.kolchenko.base.web.dto.KnowledgeDtoV1;
import av.kolchenko.base.service.HtmlKnowledgeService;
import av.kolchenko.base.web.filter.KnowledgeFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v2")
public class HtmlKnowledgeController {

    private final HtmlKnowledgeService htmlKnowledgeService;
    private final KnowledgeService knowledgeService;

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
        KnowledgeDtoV1 knowledge = htmlKnowledgeService.getKnowledgeRaw(id); // Получаем оригинальный markdown
        model.addAttribute("knowledge", knowledge);
        model.addAttribute("id", id);
        return "knowledge-edit-form";
    }

    @PostMapping("/{id}/edit")
    public String saveEditedKnowledge(@PathVariable Long id,
                                      @ModelAttribute KnowledgeDtoV1 updatedKnowledge,
                                      Model model) {
        htmlKnowledgeService.updateKnowledge(id, updatedKnowledge);
        //return "redirect:/api/v2/" + id; // Перенаправляем на страницу просмотра
        return "redirect:/api/v2/all"; // Перенаправляем на список
    }

    @GetMapping("/all")
    public String getAllKnowledge(@ModelAttribute KnowledgeFilter filter,
                                  @PageableDefault(size = 10) Pageable pageable,
                                  Model model) {
        Page<KnowledgeDtoV1> knowledgePage = knowledgeService.getAll(filter, pageable);
        model.addAttribute("knowledgePage", knowledgePage);
        model.addAttribute("filter", filter); // Pass filter to the form
        return "knowledge-list"; // New Thymeleaf template
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("knowledge", new KnowledgeDtoV1(0L, "", "", false, null));
        return "knowledge-create-form"; // Reuse or create a new form for creation
    }

    @PostMapping("/new")
    public String createKnowledge(@ModelAttribute KnowledgeDtoV1 newKnowledge) {
        knowledgeService.create(newKnowledge);
        return "redirect:/api/v2/all"; // Redirect to the list page
    }

    @PostMapping("/{id}/delete")
    public String deleteKnowledge(@PathVariable Long id) {
        knowledgeService.delete(id);
        return "redirect:/api/v2/all"; // Redirect to the list page
    }
}