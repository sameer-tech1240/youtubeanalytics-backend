package com.youtube.dto.response;

import lombok.Data;

@Data
public class VideoResponseDto {

    private String videoId;
    private String title;
    private long views;
    private long likes;
    private long comments;

    
}
