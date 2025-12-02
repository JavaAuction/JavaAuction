package com.javaauction.chatservice.infrastructure.repository;

import com.javaauction.chatservice.domain.entity.Chatting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChattingJpaRepository extends JpaRepository<Chatting, UUID>, ChattingRepository{

}
