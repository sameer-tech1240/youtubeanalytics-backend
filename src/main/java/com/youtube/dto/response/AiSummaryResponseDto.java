package com.youtube.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({ "summary", "keyPoints" })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiSummaryResponseDto {

	private String summary;
	private List<String> keyPoints;
}