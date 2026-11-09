package com.oex.result.controller;

import com.oex.result.dto.request.SubmitResultRequest;
import com.oex.result.dto.response.ResultResponse;
import com.oex.result.service.ResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @PostMapping
    public ResponseEntity<ResultResponse> submitResult(
            @Valid @RequestBody SubmitResultRequest request) {

        ResultResponse response = resultService.submitResult(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse> getResultById(@PathVariable Long id) {
        return ResponseEntity.ok(resultService.getResultById(id));
    }

}
