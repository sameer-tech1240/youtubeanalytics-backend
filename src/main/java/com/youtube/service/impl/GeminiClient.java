package com.youtube.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.youtube.dto.response.AiSummaryResponseDto;

@Service
public class GeminiClient {

	@Value("${gemini.api.key}")
	private String apiKey;

	@Value("${gemini.api.url}")
	private String apiUrl;

	@Autowired
	private RestTemplate restTemplate;

	/* ================= MAIN METHOD ================= */

	public AiSummaryResponseDto summarizeWithKeyPoints(String extractedContent) {

		String prompt = buildStrictPrompt(extractedContent);

		JSONObject requestBody = new JSONObject().put("contents", new JSONArray()
				.put(new JSONObject().put("parts", new JSONArray().put(new JSONObject().put("text", prompt)))));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

		String url = apiUrl + "?key=" + apiKey;

		String response = restTemplate.postForObject(url, entity, String.class);

		return parseGeminiResponse(response);
	}

	/* ================= PROMPT ================= */

	private String buildStrictPrompt(String content) {

		return """
				You are an AI content analyst.

				Rules:
				- Use ONLY the information provided.
				- Do NOT guess or assume anything.
				- Do NOT label content as educational unless clearly stated.
				- If information is insufficient, say so clearly.

				Your task:
				1. Identify what type of content this is (video, image, article, reel, webpage, etc).
				2. Write a clear, human-readable summary describing what this content is about.
				3. Extract 5–8 factual key points that are actually mentioned or implied.
				4. Do NOT invent topics or context.

				Return STRICT JSON only in the format:
				{
				  "summary": "...",
				  "keyPoints": ["...", "..."]
				}

				CONTENT:
				""" + content;
	}

	/* ================= RESPONSE PARSER ================= */

	private AiSummaryResponseDto parseGeminiResponse(String response) {

		JSONObject root = new JSONObject(response);

		String text = root.getJSONArray("candidates").getJSONObject(0).getJSONObject("content").getJSONArray("parts")
				.getJSONObject(0).getString("text");

		// Gemini sometimes returns extra text — extract JSON safely
		int jsonStart = text.indexOf("{");
		int jsonEnd = text.lastIndexOf("}");

		if (jsonStart == -1 || jsonEnd == -1) {
			throw new RuntimeException("Invalid Gemini response format");
		}

		String jsonString = text.substring(jsonStart, jsonEnd + 1);

		JSONObject json = new JSONObject(jsonString);

		String summary = json.optString("summary", "");

		List<String> keyPoints = new ArrayList<>();
		JSONArray pointsArray = json.optJSONArray("keyPoints");
		if (pointsArray != null) {
			for (int i = 0; i < pointsArray.length(); i++) {
				keyPoints.add(pointsArray.getString(i));
			}
		}

		return new AiSummaryResponseDto(summary, keyPoints);
	}

	 
}
