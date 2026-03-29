package com.matthewmcroberts.rankmanager.dto.mapper;

import com.matthewmcroberts.rankmanager.dto.PlayerRankAssignment;
import com.matthewmcroberts.rankmanager.dto.Rank;

/**
 * Maps service-layer/common types to REST DTOs.
 * RankService already returns a "Rank" type and a "PlayerRankAssignment" type
 * (not the Mongo RankObject / PlayerRankAssignmentObject). Our controller
 * wants to convert those to the REST DTOs in this package, which currently
 * also use the names Rank and PlayerRankAssignment. To avoid conflicts, we
 * treat the overloads as identity mappings for now.
 */
public interface RankRestMapper {
    // Map service/common Rank to REST Rank DTO
    Rank toDto(Rank rank);

    // Map service/common PlayerRankAssignment to REST PlayerRankAssignment DTO
    PlayerRankAssignment toAssignmentDto(PlayerRankAssignment assignment);
}
