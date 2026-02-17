package com.youtube.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiSummaryRequestDto {

	@NotBlank(message = "URL is required")
	private String url;

}
