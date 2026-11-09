package com.oex.notification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResultNotificationRequest {

    @NotNull(message = "Student id is required")
    private Long studentId;

    @NotBlank(message = "Exam title is required")
    private String examTitle;

    @NotNull(message = "Score is required")
    private Integer score;

    @NotNull(message = "Total marks is required")
    private Integer totalMarks;

    @NotNull(message = "Passed flag is required")
    private Boolean passed;

}
