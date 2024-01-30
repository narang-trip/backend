package com.ssafy.messageservice.api.controller;

import com.ssafy.messageservice.api.response.ChatListResponse;
import com.ssafy.messageservice.api.response.ChatroomListMidResponse;
import com.ssafy.messageservice.api.response.ChatroomListResponse;
import com.ssafy.messageservice.api.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/message")
public class ChatController {
    private final ChatService chatService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    // 채팅방 리스트
    @GetMapping("/list/{userId}")
    public ResponseEntity<ChatroomListResponse> getLatestChatsByUserId(@PathVariable String userId) {
        ChatroomListResponse chatroomListResponse = chatService.getLatestChatsByUserId(userId);
        return ResponseEntity.ok(chatroomListResponse);
    }

    // 채팅방 조회 (해당 채팅방의 채팅 불러오기)
    @GetMapping("/{chatroomId}")
    public ResponseEntity<ChatListResponse> getChatMessagesByChatroomId(@PathVariable String chatroomId, @RequestParam(defaultValue = "0") int page) {
        ChatListResponse chatPage = chatService.getChatMessagesByChatroomId(chatroomId, page);
        return ResponseEntity.ok(chatPage);
    }

    // 채팅 생성 -> stomp으로 처리
//    @PostMapping("/{chatroomId}/chat")
//    public ResponseEntity<String> postChatMessageByChatroomId(@PathVariable String chatroomId) {
//        return ResponseEntity.ok("success");
//    }
}