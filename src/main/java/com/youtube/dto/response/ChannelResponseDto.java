package com.youtube.dto.response;

import lombok.Data;

@Data
public class ChannelResponseDto {

	private String channelId;
	private String name;
	private String description;
	private long subscribers;
	private long views;
	private long videoCount;

}
