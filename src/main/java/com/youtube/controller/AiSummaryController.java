package com.youtube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.youtube.dto.request.AiSummaryRequestDto;
import com.youtube.dto.response.AiSummaryResponseDto;
import com.youtube.service.AiSummaryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ai")
public class AiSummaryController {

	@Autowired
	private AiSummaryService aiSummaryService;

	@PostMapping("/summarize")
	public ResponseEntity<AiSummaryResponseDto> summarize(@RequestBody @Valid AiSummaryRequestDto request) {

		return ResponseEntity.ok(aiSummaryService.summarizeFromUrl(request.getUrl()));
	}
}
