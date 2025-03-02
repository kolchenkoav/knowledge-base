package av.kolchenko.base.web.dto;

import av.kolchenko.base.entity.TopicType;

import java.util.Objects;

public class KnowledgeDtoV1 {
    private Long id; // Add ID field
    private final String question;
    private final String answer;
    private String shortAnswer; // Изменено на не-final для гибкости
    private final Boolean bookmark;
    private final TopicType topic;

//    public KnowledgeDtoV1(String question, String answer, Boolean bookmark, TopicType topic) {
//        this.question = question;
//        this.answer = answer;
//        this.shortAnswer = truncateAnswer(answer); // Обрезаем ответ
//        this.bookmark = bookmark;
//        this.topic = topic;
//    }

    // Метод для обрезки текста
    private String truncateAnswer(String fullAnswer) {
        if (fullAnswer == null || fullAnswer.length() <= 100) {
            return fullAnswer;
        }
        return fullAnswer.substring(0, 100) + "..."; // Обрезаем до 100 символов и добавляем многоточие
    }

    public KnowledgeDtoV1(Long id, String question, String answer, Boolean bookmark, TopicType topic) {
            this.id = id;
            this.question = question;
            this.answer = answer;
            this.shortAnswer = truncateAnswer(answer); // Обрезаем ответ
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

    public String getShortAnswer() {
        return shortAnswer;
    }

    public void setShortAnswer(String shortAnswer) {
        this.shortAnswer = shortAnswer;
    }

    // Update equals, hashCode, and toString to include id...
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