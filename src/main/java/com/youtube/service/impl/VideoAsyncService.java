package com.youtube.service.impl;

import java.time.Instant;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.youtube.entity.Video;
import com.youtube.repository.VideoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoAsyncService {

	private static final Logger log = LoggerFactory.getLogger(VideoAsyncService.class);

	private final VideoRepository videoRepo;
	private final RestTemplate restTemplate;

	@Value("${youtube.api.key}")
	private String apiKey;

	@Value("${youtube.api.base-url}")
	private String baseUrl;

	@Async
	public void updateVideosAsync(String channelId, List<String> latestVideoIds) {

		log.info("ASYNC VIDEO UPDATE STARTED | channelId={}", channelId);

		// 1️⃣ Save / update latest 10
		for (String videoId : latestVideoIds) {
		    fetchAndSaveVideoStats(videoId, channelId);
		}


		// 2️⃣ Delete old videos
		List<Video> dbVideos = videoRepo.findByChannelIdOrderByPublishedAtDesc(channelId);

		for (Video v : dbVideos) {
			if (!latestVideoIds.contains(v.getVideoId())) {
				videoRepo.delete(v);
				log.info("Deleted old video={}", v.getVideoId());
			}
		}

		log.info("ASYNC VIDEO UPDATE COMPLETED | channelId={}", channelId);
	}

	/* ================= INTERNAL ================= */

	private void fetchAndSaveVideoStats(String videoId, String channelId) {

		String url = baseUrl + "/videos?part=snippet,statistics&id=" + videoId + "&key=" + apiKey;

		JSONObject json = new JSONObject(restTemplate.getForObject(url, String.class));

		JSONArray items = json.getJSONArray("items");
		if (items.isEmpty())
			return;

		JSONObject item = items.getJSONObject(0);
		String publishedAtStr = item.getJSONObject("snippet").getString("publishedAt");
		Video v = new Video();
		v.setVideoId(videoId); // PK
		v.setChannelId(channelId);
		v.setTitle(item.getJSONObject("snippet").getString("title"));
		v.setPublishedAt(Instant.parse(publishedAtStr));

		JSONObject s = item.getJSONObject("statistics");
		v.setViews(s.optLong("viewCount"));
		v.setLikes(s.optLong("likeCount"));
		v.setComments(s.optLong("commentCount"));

		videoRepo.save(v); // UPSERT
	}
}
