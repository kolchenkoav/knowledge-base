package av.kolchenko.base.repository;

import av.kolchenko.base.entity.Knowledge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface KnowledgeRepository extends JpaRepository<Knowledge, Long>, JpaSpecificationExecutor<Knowledge> {
}