package com.oex.result.service;

import com.oex.result.dto.request.SubmitResultRequest;
import com.oex.result.dto.response.ResultResponse;

public interface ResultService {

    ResultResponse submitResult(SubmitResultRequest request);

    ResultResponse getResultById(Long id);

}
