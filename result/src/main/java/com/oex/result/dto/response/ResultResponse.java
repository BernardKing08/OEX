package com.oex.result.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {

    private Long id;
    private Long studentId;
    private Long examId;
    private String examTitle;
    private Integer score;
    private Integer totalMarks;
    private Boolean passed;
    private LocalDateTime createdAt;

}
