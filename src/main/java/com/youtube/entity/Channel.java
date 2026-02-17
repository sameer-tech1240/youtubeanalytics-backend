package com.youtube.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Channel {

    @Id
    private String channelId;

    private String name;

    @Column(length = 5000)
    private String description;

    private long subscribers;
    private long views;
    private long videoCount;
}

