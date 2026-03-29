package com.matthewmcroberts.rankmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a player-to-rank assignment resource returned from the API.
 * Used as the body for all assignment read and write endpoints.
 * {@code assignedById} is nullable — absent when the assignment has no audited assigner.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRankAssignment {
    private String playerId;
    private String rankId;
    private String assignedById;
}
