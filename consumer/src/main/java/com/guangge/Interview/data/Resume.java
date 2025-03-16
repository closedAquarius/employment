package com.guangge.Interview.data;

import jakarta.persistence.*;

@Entity
@Table(name = "resumes")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 姓名
    @Column(name = "candidate_name", nullable = false, unique = true)
    private String name;

    // 简历
    @Column(name = "raw_text", nullable = false)
    private String rawText;

    // 笔试成绩
    @Column(name = "score", nullable = true)
    private int  score;

    // 邮箱
    @Column(name = "email", nullable = true)
    private String email;

    @Column(name = "inter_view_status", nullable = true)
    @Convert(converter = InterViewStatusConverter.class)
    private InterViewStatus interViewStatus;

    // 笔试反馈
    @Column(name = "evaluate", nullable = true)
    private String evaluate;

    // 笔试反馈mp3
    @Column(name = "mp3_path", nullable = true)
    private String mp3Path;

    // 笔试反馈
    @Column(name = "interview_evaluate", nullable = true)
    private String interviewEvaluate;

    @Column(name = "is_done_status", nullable = true)
    @Convert(converter = IsDoneStatusConverter.class)
    private IsDoneStatus isDoneStatus;

    public Resume() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public InterViewStatus getInterViewStatus() {
        return interViewStatus;
    }

    public void setInterViewStatus(InterViewStatus interViewStatus) {
        this.interViewStatus = interViewStatus;
    }

    public String getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(String evaluate) {
        this.evaluate = evaluate;
    }

    public String getMp3Path() {
        return mp3Path;
    }

    public void setMp3Path(String mp3Path) {
        this.mp3Path = mp3Path;
    }

    public String getInterviewEvaluate() {
        return interviewEvaluate;
    }

    public void setInterviewEvaluate(String interviewEvaluate) {
        this.interviewEvaluate = interviewEvaluate;
    }

    public IsDoneStatus getIsDoneStatus() {
        return isDoneStatus;
    }

    public void setIsDoneStatus(IsDoneStatus isDoneStatus) {
        this.isDoneStatus = isDoneStatus;
    }
}
