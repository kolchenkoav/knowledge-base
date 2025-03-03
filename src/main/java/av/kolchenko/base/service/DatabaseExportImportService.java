package av.kolchenko.base.service;

import av.kolchenko.base.web.dto.KnowledgeDtoV1;

import java.util.List;

public interface DatabaseExportImportService {
    void exportToJson(String filePath) throws Exception;
    List<KnowledgeDtoV1> importFromJson(String filePath) throws Exception;
}