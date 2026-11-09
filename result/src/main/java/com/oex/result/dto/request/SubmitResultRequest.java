package com.oex.result.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitResultRequest {

    @NotNull(message = "Student id is required")
    private Long studentId;

    @NotNull(message = "Exam id is required")
    private Long examId;

    @NotBlank(message = "Exam title is required")
    private String examTitle;

    @NotNull(message = "Score is required")
    private Integer score;

    @NotNull(message = "Total marks is required")
    private Integer totalMarks;

}
