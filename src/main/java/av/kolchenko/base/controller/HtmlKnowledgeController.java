// src/main/java/av/kolchenko/base/controller/HtmlKnowledgeController.java
package av.kolchenko.base.controller;

import av.kolchenko.base.KnowledgeDtoV1;
import av.kolchenko.base.service.HtmlKnowledgeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v2")
public class HtmlKnowledgeController {

    private final HtmlKnowledgeService htmlKnowledgeService;

    public HtmlKnowledgeController(HtmlKnowledgeService htmlKnowledgeService) {
        this.htmlKnowledgeService = htmlKnowledgeService;
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
        return "redirect:/api/v2/" + id; // Перенаправляем на страницу просмотра
    }
}