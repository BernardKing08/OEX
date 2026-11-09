package com.oex.result.service.impl;

import com.oex.result.client.AuthenticationClient;
import com.oex.result.client.NotificationClient;
import com.oex.result.dto.UserInfo;
import com.oex.result.dto.request.ResultNotificationRequest;
import com.oex.result.dto.request.SubmitResultRequest;
import com.oex.result.dto.response.ResultResponse;
import com.oex.result.exception.ResourceNotFoundException;
import com.oex.result.mapper.ResultMapper;
import com.oex.result.model.Result;
import com.oex.result.repository.ResultRepository;
import com.oex.result.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {

    private static final double PASS_THRESHOLD = 0.4;

    private final ResultRepository resultRepository;
    private final ResultMapper resultMapper;
    private final AuthenticationClient authenticationClient;
    private final NotificationClient notificationClient;

    @Override
    public ResultResponse submitResult(SubmitResultRequest request) {
        UserInfo student = authenticationClient.getUser(request.getStudentId());

        boolean passed = request.getScore() >= request.getTotalMarks() * PASS_THRESHOLD;

        Result result = Result.builder()
                .studentId(request.getStudentId())
                .examId(request.getExamId())
                .examTitle(request.getExamTitle())
                .score(request.getScore())
                .totalMarks(request.getTotalMarks())
                .passed(passed)
                .build();

        Result savedResult = resultRepository.save(result);

        notificationClient.sendResultNotification(ResultNotificationRequest.builder()
                .studentId(student.getId())
                .examTitle(savedResult.getExamTitle())
                .score(savedResult.getScore())
                .totalMarks(savedResult.getTotalMarks())
                .passed(savedResult.getPassed())
                .build());

        return resultMapper.toResponse(savedResult);
    }

    @Override
    public ResultResponse getResultById(Long id) {
        Result result = resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Result not found with id: " + id));

        return resultMapper.toResponse(result);
    }

}
