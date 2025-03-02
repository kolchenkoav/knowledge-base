package av.kolchenko.base.web.controller;

import av.kolchenko.base.web.dto.KnowledgeDtoV1;
import av.kolchenko.base.web.filter.KnowledgeFilter;
import av.kolchenko.base.service.KnowledgeService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @GetMapping
    public PagedModel<KnowledgeDtoV1> getAll(@ParameterObject @ModelAttribute KnowledgeFilter filter, @ParameterObject Pageable pageable) {
        Page<KnowledgeDtoV1> knowledgeDtoV1s = knowledgeService.getAll(filter, pageable);
        return new PagedModel<>(knowledgeDtoV1s);
    }

    @GetMapping("/{id}")
    public KnowledgeDtoV1 getOne(@PathVariable Long id) {
        return knowledgeService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<KnowledgeDtoV1> getMany(@RequestParam List<Long> ids) {
        return knowledgeService.getMany(ids);
    }

    @PostMapping
    public KnowledgeDtoV1 create(@RequestBody KnowledgeDtoV1 dto) {
        return knowledgeService.create(dto);
    }

    @PatchMapping("/{id}")
    public KnowledgeDtoV1 patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return knowledgeService.patch(id, patchNode);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return knowledgeService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    public KnowledgeDtoV1 delete(@PathVariable Long id) {
        return knowledgeService.delete(id);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        knowledgeService.deleteMany(ids);
    }
}
