// src/main/java/av/kolchenko/base/web/controller/RootController.java
package av.kolchenko.base.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String redirectToKnowledgeList() {
        return "redirect:/api/v2/all"; // Редирект на страницу списка знаний
    }
}