package com.youtube.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.youtube.dto.response.AiSummaryResponseDto;
import com.youtube.extractor.ContentExtractor;
import com.youtube.service.AiSummaryService;

@Service
public class AiSummaryServiceImpl implements AiSummaryService {

    private static final Logger log =
            LoggerFactory.getLogger(AiSummaryServiceImpl.class);

    @Autowired
    private ContentExtractor contentExtractor;

    @Autowired
    private GeminiClient geminiClient;

    @Override
    public AiSummaryResponseDto summarizeFromUrl(String url) {

        log.info("AI summary request received for URL: {}", url);

        // 1️ Extract clean content
        String extractedText = contentExtractor.extractText(url);
        log.debug("Extracted content length: {}", extractedText.length());

        // 2️ Gemini call (DIRECT DTO)
        AiSummaryResponseDto response =
                geminiClient.summarizeWithKeyPoints(extractedText);

        // 3️ Logs
        log.info("AI Summary:\n{}", response.getSummary());
        log.info("AI Key Points: {}", response.getKeyPoints());

        // 4️ API Response
        return response;
    }
}
