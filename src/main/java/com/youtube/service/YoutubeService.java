package com.youtube.service;

import java.util.List;

import com.youtube.dto.response.ChannelResponseDto;
import com.youtube.dto.response.VideoResponseDto;

public interface YoutubeService {

	ChannelResponseDto analyzeChannel(String channelInput);

	List<VideoResponseDto> getVideosByChannel(String channelId);
}
