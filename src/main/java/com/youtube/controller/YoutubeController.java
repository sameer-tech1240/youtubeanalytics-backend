package com.youtube.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.youtube.dto.request.YoutubeAnalyzeRequestDto;
import com.youtube.dto.response.ChannelResponseDto;
import com.youtube.dto.response.VideoResponseDto;
import com.youtube.service.YoutubeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/youtube")
public class YoutubeController {

	private static final Logger log = LoggerFactory.getLogger(YoutubeController.class);

	@Autowired
	private YoutubeService youtubeService;

	@PostMapping("/analyze")
	public ResponseEntity<ChannelResponseDto> analyze(@Valid @RequestBody YoutubeAnalyzeRequestDto request) {

		log.info("POST /analyze called | input={}", request.getChannelInput());

		ChannelResponseDto response = youtubeService.analyzeChannel(request.getChannelInput());

		log.info("POST /analyze success | channelId={}", response.getChannelId());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{channelId}/videos")
	public ResponseEntity<List<VideoResponseDto>> videos(@PathVariable("channelId") String channelId) {

		log.info("GET /videos called | channelId={}", channelId);

		return ResponseEntity.ok(youtubeService.getVideosByChannel(channelId));
	}
}
