// src/main/java/av/kolchenko/base/web/dto/KnowledgeDtoV1.java
package av.kolchenko.base.web.dto;

import av.kolchenko.base.entity.TopicType;

import java.util.Objects;

public class KnowledgeDtoV1 {
    private Long id;
    private String question;
    private String answer;
    private String shortAnswer; // Вычисляемое поле
    private Boolean bookmark;
    private TopicType topic;

    // Конструктор по умолчанию для Spring
    public KnowledgeDtoV1() {
    }

    // Конструктор с параметрами для создания объекта вручную
    public KnowledgeDtoV1(Long id, String question, String answer, Boolean bookmark, TopicType topic) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.shortAnswer = truncateAnswer(answer); // Автоматически генерируем shortAnswer
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

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
        this.shortAnswer = truncateAnswer(answer); // Обновляем shortAnswer при изменении answer
    }

    public String getShortAnswer() {
        return shortAnswer;
    }

    // Удаляем публичный setter для shortAnswer, так как оно вычисляемое
    private void setShortAnswer(String shortAnswer) {
        this.shortAnswer = shortAnswer;
    }

    public Boolean getBookmark() {
        return bookmark;
    }

    public void setBookmark(Boolean bookmark) {
        this.bookmark = bookmark;
    }

    public TopicType getTopic() {
        return topic;
    }

    public void setTopic(TopicType topic) {
        this.topic = topic;
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
                Objects.equals(this.shortAnswer, entity.shortAnswer) &&
                Objects.equals(this.bookmark, entity.bookmark) &&
                Objects.equals(this.topic, entity.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, question, answer, shortAnswer, bookmark, topic);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "question = " + question + ", " +
                "answer = " + answer + ", " +
                "shortAnswer = " + shortAnswer + ", " +
                "bookmark = " + bookmark + ", " +
                "topic = " + topic + ")";
    }
}