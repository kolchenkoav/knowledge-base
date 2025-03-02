package av.kolchenko.base.mapper;

import av.kolchenko.base.KnowledgeDtoV1;
import av.kolchenko.base.entity.Knowledge;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface KnowledgeMapper {
    Knowledge toEntity(KnowledgeDtoV1 knowledgeDtoV1);

    KnowledgeDtoV1 toKnowledgeDtoV1(Knowledge knowledge);

    Knowledge updateWithNull(KnowledgeDtoV1 knowledgeDtoV1, @MappingTarget Knowledge knowledge);
}