package av.kolchenko.base;

import av.kolchenko.base.entity.TopicType;

import java.util.Objects;

/**
 * DTO for {@link av.kolchenko.base.entity.Knowledge}
 */
public class KnowledgeDtoV1 {
    private final String question;
    private final String answer;
    private final Boolean bookmark;
    private final TopicType topic;

    public KnowledgeDtoV1(String question, String answer, Boolean bookmark, TopicType topic) {
        this.question = question;
        this.answer = answer;
        this.bookmark = bookmark;
        this.topic = topic;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public Boolean getBookmark() {
        return bookmark;
    }

    public TopicType getTopic() {
        return topic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnowledgeDtoV1 entity = (KnowledgeDtoV1) o;
        return Objects.equals(this.question, entity.question) &&
                Objects.equals(this.answer, entity.answer) &&
                Objects.equals(this.bookmark, entity.bookmark) &&
                Objects.equals(this.topic, entity.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, answer, bookmark, topic);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "question = " + question + ", " +
                "answer = " + answer + ", " +
                "bookmark = " + bookmark + ", " +
                "topic = " + topic + ")";
    }
}