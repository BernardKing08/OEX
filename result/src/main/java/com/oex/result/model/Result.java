package com.oex.result.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "results")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private Long examId;

    @Column(nullable = false)
    private String examTitle;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Integer totalMarks;

    @Column(nullable = false)
    private Boolean passed;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
