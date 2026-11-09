package com.oex.result.mapper;

import com.oex.result.dto.response.ResultResponse;
import com.oex.result.model.Result;
import org.springframework.stereotype.Component;

@Component
public class ResultMapper {

    public ResultResponse toResponse(Result result) {
        return ResultResponse.builder()
                .id(result.getId())
                .studentId(result.getStudentId())
                .examId(result.getExamId())
                .examTitle(result.getExamTitle())
                .score(result.getScore())
                .totalMarks(result.getTotalMarks())
                .passed(result.getPassed())
                .createdAt(result.getCreatedAt())
                .build();
    }

}
