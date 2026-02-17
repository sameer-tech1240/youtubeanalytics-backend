package com.youtube.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.youtube.dto.response.ChannelResponseDto;
import com.youtube.dto.response.VideoResponseDto;
import com.youtube.entity.Channel;
import com.youtube.entity.Video;
import com.youtube.repository.ChannelRepository;
import com.youtube.repository.VideoRepository;
import com.youtube.service.YoutubeService;

@Service
public class YoutubeServiceImpl implements YoutubeService {

	private static final Logger log = LoggerFactory.getLogger(YoutubeServiceImpl.class);

	@Value("${youtube.api.key}")
	private String apiKey;

	@Value("${youtube.api.base-url}")
	private String baseUrl;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ChannelRepository channelRepo;

	@Autowired
	private VideoRepository videoRepo;

	@Autowired
	private VideoAsyncService videoAsyncService;

	/* ================= ANALYZE CHANNEL ================= */

	@Override
	public ChannelResponseDto analyzeChannel(String input) {

		log.info("ANALYZE START | input={}", input);

		String query = normalizeInput(input);
		String channelId = fetchChannelId(query);

		// 1️⃣ Channel stats ALWAYS update (fast)
		Channel channel = fetchAndSaveChannel(channelId);

		// 2️⃣ Latest 10 video IDs (fast)
		List<String> latestVideoIds =
		        fetchLatest20VideoIds(channelId);

		// 3️⃣ 🔥 HEAVY WORK → BACKGROUND
		videoAsyncService.updateVideosAsync(channelId, latestVideoIds);

		log.info("ANALYZE COMPLETED (async running) | channelId={}", channelId);
		return mapToChannelDto(channel);
	}

	/* ================= GET VIDEOS ================= */

	@Override
	public List<VideoResponseDto> getVideosByChannel(String channelId) {

		if (channelId == null || channelId.isBlank()) {
			return Collections.emptyList();
		}

		return videoRepo.findByChannelIdOrderByPublishedAtDesc(channelId).stream().map(this::mapToVideoDto).toList();
	}

	/* ================= INTERNAL ================= */

	private String normalizeInput(String input) {
		if (input == null)
			return "";
		if (input.contains("@")) {
			return input.substring(input.lastIndexOf("@"));
		}
		return input;
	}

	/* ================= CHANNEL ================= */

	private String fetchChannelId(String query) {

		String url = baseUrl + "/search?part=snippet&type=channel&q=" + query + "&key=" + apiKey;

		JSONObject json = new JSONObject(restTemplate.getForObject(url, String.class));

		JSONArray items = json.getJSONArray("items");
		if (items.isEmpty()) {
			throw new RuntimeException("No channel found");
		}

		return items.getJSONObject(0).getJSONObject("id").getString("channelId");
	}

	private String fetchUploadsPlaylistId(String channelId) {

		String url = baseUrl + "/channels?part=contentDetails&id=" + channelId + "&key=" + apiKey;

		JSONObject json = new JSONObject(restTemplate.getForObject(url, String.class));

		return json.getJSONArray("items").getJSONObject(0).getJSONObject("contentDetails")
				.getJSONObject("relatedPlaylists").getString("uploads");
	}

	private Channel fetchAndSaveChannel(String channelId) {

		String url = baseUrl + "/channels?part=snippet,statistics&id=" + channelId + "&key=" + apiKey;

		JSONObject item = new JSONObject(restTemplate.getForObject(url, String.class)).getJSONArray("items")
				.getJSONObject(0);

		Channel c = new Channel();
		c.setChannelId(channelId);
		c.setName(item.getJSONObject("snippet").getString("title"));
		c.setDescription(item.getJSONObject("snippet").optString("description", ""));

		JSONObject s = item.getJSONObject("statistics");
		c.setSubscribers(s.optLong("subscriberCount"));
		c.setViews(s.optLong("viewCount"));
		c.setVideoCount(s.optLong("videoCount"));

		return channelRepo.save(c); // UPSERT
	}

	/* ================= VIDEOS ================= */

	private List<String> fetchLatest20VideoIds(String channelId) {

		String uploadsPlaylistId = fetchUploadsPlaylistId(channelId);

		String url = baseUrl + "/playlistItems?part=contentDetails" + "&playlistId=" + uploadsPlaylistId
				+ "&maxResults=20" + "&key=" + apiKey;

		JSONObject json = new JSONObject(restTemplate.getForObject(url, String.class));

		JSONArray items = json.getJSONArray("items");

		List<String> videoIds = new ArrayList<>();

		for (int i = 0; i < items.length(); i++) {
			videoIds.add(items.getJSONObject(i).getJSONObject("contentDetails").getString("videoId"));
		}

		log.info("Fetched {} videos from uploads playlist", videoIds.size());
		return videoIds;
	}

	/* ================= DTO ================= */

	private ChannelResponseDto mapToChannelDto(Channel c) {
		ChannelResponseDto dto = new ChannelResponseDto();
		dto.setChannelId(c.getChannelId());
		dto.setName(c.getName());
		dto.setDescription(c.getDescription());
		dto.setSubscribers(c.getSubscribers());
		dto.setViews(c.getViews());
		dto.setVideoCount(c.getVideoCount());
		return dto;
	}

	private VideoResponseDto mapToVideoDto(Video v) {
		VideoResponseDto dto = new VideoResponseDto();
		dto.setVideoId(v.getVideoId());
		dto.setTitle(v.getTitle());
		dto.setViews(v.getViews());
		dto.setLikes(v.getLikes());
		dto.setComments(v.getComments());
		return dto;
	}
}
