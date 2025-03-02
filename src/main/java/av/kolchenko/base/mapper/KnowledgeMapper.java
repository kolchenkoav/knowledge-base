package av.kolchenko.base.mapper;

import av.kolchenko.base.web.dto.KnowledgeDtoV1;
import av.kolchenko.base.entity.Knowledge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface KnowledgeMapper {
    Knowledge toEntity(KnowledgeDtoV1 knowledgeDtoV1);

    @Mapping(source = "id", target = "id")
    @Mapping(target = "shortAnswer", expression = "java(truncateAnswer(knowledge.getAnswer()))") // Добавляем обрезку
    KnowledgeDtoV1 toKnowledgeDtoV1(Knowledge knowledge);

    Knowledge updateWithNull(KnowledgeDtoV1 knowledgeDtoV1, @MappingTarget Knowledge knowledge);

    // Вспомогательный метод для обрезки
    default String truncateAnswer(String answer) {
        if (answer == null || answer.length() <= 100) {
            return answer;
        }
        return answer.substring(0, 100) + "...";
    }
}