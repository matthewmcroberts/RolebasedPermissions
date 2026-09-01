package com.matthewmcroberts.rankmanager.events;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublisher {
    private final SimpMessagingTemplate messagingTemplate;

    public void publishPlayerRankAssign(PlayerRankAssignEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/player-rank-assign",
                event
        );
    }

    public void publishPlayerRankRemove(PlayerRankRemoveEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/player-rank-remove",
                event
        );
    }

    public void publishRankCreate(RankCreateEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/rank-create",
                event
        );
    }

    public void publishRankDelete(RankDeleteEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/rank-delete",
                event
        );
    }

    public void publishRankInheritanceUpdate(RankInheritanceUpdateEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/rank-inheritance",
                event
        );
    }

    public void publishRankPermissionUpdate(RankPermissionUpdateEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/rank-permission",
                event
        );
    }

    public void publishRankUpdate(RankUpdateEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/rank-update",
                event
        );
    }
}