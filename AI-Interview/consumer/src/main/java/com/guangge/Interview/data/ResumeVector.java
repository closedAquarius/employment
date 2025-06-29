package com.guangge.Interview.data;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "resume_vectors")
public class ResumeVector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(value = VectorType.class)
    @Column(name = "embedding", columnDefinition = "vector(50)")
    private float[] embedding;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "resume_id", referencedColumnName = "id")
    private Resume resume;


    // 必须有无参构造函数
    public ResumeVector() {}

    public ResumeVector(float[] embedding, Resume resume) {
        this.embedding = embedding;
        this.resume = resume;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

}
