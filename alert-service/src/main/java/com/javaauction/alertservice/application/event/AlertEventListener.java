package com.javaauction.alertservice.application.event;

import com.javaauction.alertservice.application.client.SlackClientV1;
import com.javaauction.alertservice.application.client.UserClientV1;
import com.javaauction.alertservice.domain.entity.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEventListener {

    private final SlackClientV1 slackClient;
    private final UserClientV1 userClient;

    @Async   // 비동기
    @EventListener
    public void onAlertCreated(AlertCreatedEvent event) {
        Alert alert = event.getAlert();
        sendSlack(alert);
    }

    // 슬랙 발송
    private void sendSlack(Alert alert) {
        try {
            var repUserDto = userClient.getUser(alert.getUserId());

            if (repUserDto.getSlackId() == null || repUserDto.getSlackId().isBlank()) {
                return;
            }

            Map<String, Object> openResp = slackClient.openConversation(
                    "Bearer " + System.getenv("SLACK_BOT_TOKEN"),
                    Map.of("users", repUserDto.getSlackId())
            );

            if (openResp != null && Boolean.TRUE.equals(openResp.get("ok"))) {
                Map<String, Object> channelMap = (Map<String, Object>) openResp.get("channel");
                String channelId = (String) channelMap.get("id");

                if (channelId != null && !channelId.isBlank()) {
                    Map<String, Object> msgResp = slackClient.postMessage(
                            "Bearer " + System.getenv("SLACK_BOT_TOKEN"),
                            Map.of("channel", channelId, "text", alert.getContent())
                    );

                    if (msgResp == null || !Boolean.TRUE.equals(msgResp.get("ok"))) {
                        log.warn("Slack 메시지 전송 실패: {}", msgResp);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Slack 메시지 발송 중 예외 발생", e);
        }
    }
}