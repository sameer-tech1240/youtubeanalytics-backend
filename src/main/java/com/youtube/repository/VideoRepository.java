package com.youtube.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.youtube.entity.Video;

public interface VideoRepository extends JpaRepository<Video, String> {

	/* List<Video> findByChannelId(String channelId); */

	List<Video> findByChannelIdOrderByPublishedAtDesc(String channelId);

}
