package av.kolchenko.base.web.filter;

import av.kolchenko.base.entity.Knowledge;
import av.kolchenko.base.entity.TopicType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public record KnowledgeFilter(String questionContains, String answerContains, Boolean bookmark, TopicType topic) {
    public Specification<Knowledge> toSpecification() {
        return Specification.where(questionContainsSpec())
                .and(answerContainsSpec())
                .and(bookmarkSpec())
                .and(topicSpec());
    }

    private Specification<Knowledge> questionContainsSpec() {
        return ((root, query, cb) -> StringUtils.hasText(questionContains)
                ? cb.like(cb.lower(root.get("question")), "%" + questionContains.toLowerCase() + "%")
                : null);
    }

    private Specification<Knowledge> answerContainsSpec() {
        return ((root, query, cb) -> StringUtils.hasText(answerContains)
                ? cb.like(cb.lower(root.get("answer")), "%" + answerContains.toLowerCase() + "%")
                : null);
    }

    private Specification<Knowledge> bookmarkSpec() {
        return ((root, query, cb) -> bookmark != null
                ? cb.equal(root.get("bookmark"), bookmark)
                : null);
    }

    private Specification<Knowledge> topicSpec() {
        return ((root, query, cb) -> topic != null
                ? cb.equal(root.get("topic"), topic)
                : null);
    }
}