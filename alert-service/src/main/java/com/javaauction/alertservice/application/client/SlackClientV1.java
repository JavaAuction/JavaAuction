package com.javaauction.alertservice.application.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(
        name = "slackClient",
        url = "https://slack.com/api"
)

public interface SlackClientV1 {

    @PostMapping(value = "/conversations.open", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> openConversation(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody Map<String, Object> body
    );

    @PostMapping(value = "/chat.postMessage", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> postMessage(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody Map<String, Object> body
    );

}
