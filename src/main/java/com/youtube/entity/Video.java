package com.youtube.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Video {

	  @Id
	    private String videoId;

	    private String title;
	    private long views;
	    private long likes;
	    private long comments;
	    
	    private Instant publishedAt;


	    @Column(name = "channel_id")  
	    private String channelId;
}

