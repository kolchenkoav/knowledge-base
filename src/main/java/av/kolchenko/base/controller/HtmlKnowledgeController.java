package av.kolchenko.base.controller;

import av.kolchenko.base.KnowledgeDtoV1;
import av.kolchenko.base.service.HtmlKnowledgeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
        return "knowledge-form"; // Ссылка на имя шаблона Thymeleaf
    }
}