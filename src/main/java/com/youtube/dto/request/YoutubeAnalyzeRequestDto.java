package com.youtube.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class YoutubeAnalyzeRequestDto {

	@NotBlank(message = "Channel input is mandatory")
	@Size(min = 3, max = 200, message = "Channel input must be 3–200 characters")
	private String channelInput;

}
