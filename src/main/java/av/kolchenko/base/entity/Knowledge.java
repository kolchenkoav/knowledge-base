package av.kolchenko.base.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "knowledge")
public class Knowledge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;
    
    @Column(name = "bookmark", columnDefinition = "boolean default false")
    private Boolean bookmark;

    @Enumerated(EnumType.STRING)
    @Column(name = "topic")
    private TopicType topic;

    public TopicType getTopic() {
        return topic;
    }

    public void setTopic(TopicType topic) {
        this.topic = topic;
    }

    public Boolean getBookmark() {
        return bookmark;
    }

    public void setBookmark(Boolean bookmark) {
        this.bookmark = bookmark;
    }


    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}