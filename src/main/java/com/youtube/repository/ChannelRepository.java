package com.youtube.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.youtube.entity.Channel;

public interface ChannelRepository extends JpaRepository<Channel, String> {
}
