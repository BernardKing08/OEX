package com.oex.result.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultNotificationRequest {

    private Long studentId;
    private String examTitle;
    private Integer score;
    private Integer totalMarks;
    private Boolean passed;

}
