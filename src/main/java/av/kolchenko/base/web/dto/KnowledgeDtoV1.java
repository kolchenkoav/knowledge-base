// src/main/java/av/kolchenko/base/web/dto/KnowledgeDtoV1.java
package av.kolchenko.base.web.dto;

import av.kolchenko.base.entity.TopicType;

import java.util.Objects;

public class KnowledgeDtoV1 {
    private Long id;
    private final String question;
    private final String answer;
    private final Boolean bookmark;
    private final TopicType topic;

    public KnowledgeDtoV1(Long id, String question, String answer, Boolean bookmark, TopicType topic) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.bookmark = bookmark;
        this.topic = topic;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    // Вычисляемое поле shortAnswer
    public String getShortAnswer() {
        return truncateAnswer(answer);
    }

    private String truncateAnswer(String fullAnswer) {
        if (fullAnswer == null || fullAnswer.length() <= 100) {
            return fullAnswer;
        }
        return fullAnswer.substring(0, 100) + "...";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KnowledgeDtoV1 entity = (KnowledgeDtoV1) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.question, entity.question) &&
                Objects.equals(this.answer, entity.answer) &&
                Objects.equals(this.bookmark, entity.bookmark) &&
                Objects.equals(this.topic, entity.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, question, answer, bookmark, topic);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "question = " + question + ", " +
                "answer = " + answer + ", " +
                "shortAnswer = " + getShortAnswer() + ", " +
                "bookmark = " + bookmark + ", " +
                "topic = " + topic + ")";
    }
}