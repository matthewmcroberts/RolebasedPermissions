package com.matthewmcroberts.rankmanager.dto.mapper;

import com.matthewmcroberts.rankmanager.dto.PlayerRankAssignment;
import com.matthewmcroberts.rankmanager.dto.Rank;
import org.springframework.stereotype.Component;

/**
 * Trivial implementation of {@link RankRestMapper} that currently acts as an
 * identity mapper between the service-layer Rank / PlayerRankAssignment types
 * and the REST DTOs of the same name.
 *
 * If you later split the service/domain models from the REST DTOs, update
 * this mapper to copy fields between them instead of returning the same
 * instance.
 */
@Component
public class RankRestMapperImpl implements RankRestMapper {

    @Override
    public Rank toDto(Rank rank) {
        // For now, the service Rank and REST Rank are the same structure,
        // so we can return it directly. Change this if you introduce
        // separate domain vs API models.
        return rank;
    }

    @Override
    public PlayerRankAssignment toAssignmentDto(PlayerRankAssignment assignment) {
        // Same rationale as above: treat this as an identity mapping for now.
        return assignment;
    }
}

