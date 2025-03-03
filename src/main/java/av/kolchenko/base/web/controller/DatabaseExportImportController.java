// src/main/java/av/kolchenko/base/web/controller/DatabaseExportImportController.java
package av.kolchenko.base.web.controller;

import av.kolchenko.base.service.DatabaseExportImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v2/db")
public class DatabaseExportImportController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseExportImportController.class);

    private final DatabaseExportImportService exportImportService;

    public DatabaseExportImportController(DatabaseExportImportService exportImportService) {
        this.exportImportService = exportImportService;
    }

    @PostMapping("/export")
    public String exportDatabase(@RequestParam(value = "filePath", required = false) String filePath, Model model) {
        try {
            exportImportService.exportToJson(filePath);
            model.addAttribute("message", "База данных успешно экспортирована в " + filePath);
        } catch (Exception e) {
            logger.error("Ошибка при экспорте базы данных: {}", e.getMessage(), e);
            model.addAttribute("error", "Не удалось экспортировать базу данных: " + e.getMessage());
        }
        return "export-import-result";
    }

    @PostMapping("/import")
    public String importDatabase(@RequestParam(value = "filePath", required = false) String filePath, Model model) {
        try {
            exportImportService.importFromJson(filePath);
            model.addAttribute("message", "Данные успешно импортированы из " + filePath);
        } catch (Exception e) {
            logger.error("Ошибка при импорте базы данных: {}", e.getMessage(), e);
            model.addAttribute("error", "Не удалось импортировать данные: " + e.getMessage());
        }
        return "export-import-result";
    }
}