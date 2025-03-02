// src/main/java/av/kolchenko/base/mapper/KnowledgeMapper.java
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
    KnowledgeDtoV1 toKnowledgeDtoV1(Knowledge knowledge); // shortAnswer теперь вычисляется в DTO

    Knowledge updateWithNull(KnowledgeDtoV1 knowledgeDtoV1, @MappingTarget Knowledge knowledge);
}