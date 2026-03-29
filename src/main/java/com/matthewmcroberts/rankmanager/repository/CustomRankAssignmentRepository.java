package com.matthewmcroberts.rankmanager.repository;

import com.matthewmcroberts.rankmanager.repository.model.PlayerRankAssignmentObject;
import com.matthewmcroberts.rankmanager.repository.model.RankObject;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.List;

public interface CustomRankAssignmentRepository {

    PlayerRankAssignmentObject setPlayerRankAssignment(
            String playerId, RankObject rank, @Nullable String assignedById, Instant assignedAtTimestamp);

    boolean removePlayerRankAssignment(String playerId);

    List<PlayerRankAssignmentObject> removePlayerRankAssignments(String rankId);
}
