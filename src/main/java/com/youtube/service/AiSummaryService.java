package com.youtube.service;

import com.youtube.dto.response.AiSummaryResponseDto;

public interface AiSummaryService {
    AiSummaryResponseDto summarizeFromUrl(String url);
}

