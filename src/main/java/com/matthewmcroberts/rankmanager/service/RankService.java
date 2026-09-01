package com.matthewmcroberts.rankmanager.service;

import com.matthewmcroberts.rankmanager.RankCommon;
import com.matthewmcroberts.rankmanager.dto.PlayerRankAssignment;
import com.matthewmcroberts.rankmanager.dto.Rank;
import com.matthewmcroberts.rankmanager.events.EventPublisher;
import com.matthewmcroberts.rankmanager.events.PlayerRankAssignEvent;
import com.matthewmcroberts.rankmanager.events.PlayerRankRemoveEvent;
import com.matthewmcroberts.rankmanager.events.RankCreateEvent;
import com.matthewmcroberts.rankmanager.events.RankDeleteEvent;
import com.matthewmcroberts.rankmanager.events.RankInheritanceUpdateEvent;
import com.matthewmcroberts.rankmanager.events.RankPermissionUpdateEvent;
import com.matthewmcroberts.rankmanager.events.RankUpdateEvent;
import com.matthewmcroberts.rankmanager.exception.RankCreateException;
import com.matthewmcroberts.rankmanager.exception.RankIdNotFoundException;
import com.matthewmcroberts.rankmanager.exception.RankNotAssignedException;
import com.matthewmcroberts.rankmanager.exception.RankUpdateException;
import com.matthewmcroberts.rankmanager.repository.PlayerRankAssignmentRepository;
import com.matthewmcroberts.rankmanager.repository.RankRepository;
import com.matthewmcroberts.rankmanager.repository.model.PlayerRankAssignmentObject;
import com.matthewmcroberts.rankmanager.repository.model.RankObject;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class RankService {
    private final RankRepository rankRepository;
    private final PlayerRankAssignmentRepository playerRankAssignmentRepository;

    private final EventPublisher  eventPublisher;

    // Map a RankObject to the Rank DTO
    private Rank convertToCommonRank(final RankObject rank) {
        return Rank.builder()
                .id(rank.getRankId())
                .displayName(rank.getDisplayName())
                .priority(rank.getPriority())
                .ownPermissions(rank.getOwnPermissions())
                .effectivePermissions(rank.getEffectivePermissions())
                .inheritedRankIds(rank.getInheritedRankIds())
                .build();
    }

    // Map a PlayerRankAssignmentObject plus its Rank into the PlayerRankAssignment DTO
    private PlayerRankAssignment convertToCommonAssignment(
            final PlayerRankAssignmentObject assignmentObject, final Rank rank) {
        return PlayerRankAssignment.builder()
                .playerId(assignmentObject.getPlayerId())
                .rankId(rank.getId())
                .assignedById(assignmentObject.getAssignedById())
                .build();
    }

    public Rank createRank(
            @NonNull final String rankId,
            @NonNull final String displayName,
            final int priority,
            @NonNull final Set<String> ownPermissions,
            @NonNull final Set<String> effectivePermissions,
            @NonNull final Set<String> inheritedRankIds) {
        if (!RankCommon.isValidRankName(displayName)) {
            throw new RankCreateException(rankId, displayName, priority, RankCreateException.Reason.INVALID_NAME);
        }

        if (!RankCommon.isValidPriority(priority)) {
            throw new RankCreateException(rankId, displayName, priority, RankCreateException.Reason.INVALID_PRIORITY);
        }

        final RankObject rankObject = RankObject.builder()
                .rankId(rankId)
                .displayName(displayName)
                .priority(priority)
                .effectivePermissions(effectivePermissions)
                .ownPermissions(ownPermissions)
                .inheritedRankIds(inheritedRankIds)
                .build();

        final RankObject saved = this.rankRepository.save(rankObject);
        // After saving, recompute effective permissions for this rank
        this.rankRepository.addRankPermissions(rankId, Set.of());

        final RankObject reloaded = this.rankRepository
                .findByRankId(rankId)
                .orElse(saved);

        final Rank commonRank = convertToCommonRank(reloaded);

        eventPublisher.publishRankCreate(RankCreateEvent.builder()
                        .rank(commonRank)
                        .build());

        return commonRank;
    }

    public Rank getRankById(final String rankId) {
        RankObject rank = this.findRankObjectByIdOrThrow(rankId);
        return this.convertToCommonRank(rank);
    }

    public Rank getRankByName(final String rankName) {
        final Optional<RankObject> rankOptional = this.rankRepository.findByDisplayName(rankName);
        if (rankOptional.isEmpty()) {
            throw new RankIdNotFoundException(rankName);
        }
        return this.convertToCommonRank(rankOptional.get());
    }

    public List<Rank> getAllRanks() {
        return this.rankRepository.findAll().stream()
                .map(this::convertToCommonRank)
                .toList();
    }

    public Rank updateRankDisplayName(final String rankId, final String newDisplayName) {
        final RankObject rank = this.findRankObjectByIdOrThrow(rankId);

        if (!RankCommon.isValidRankName(newDisplayName)) {
            throw new RankUpdateException(rankId, RankUpdateException.Reason.INVALID_NAME);
        }

        final RankObject saved = this.rankRepository.updateRankDisplayName(rank.getRankId(), newDisplayName);

        final Rank commonRank = convertToCommonRank(saved);

        eventPublisher.publishRankUpdate(RankUpdateEvent.builder()
                        .rank(commonRank)
                        .reason(RankUpdateEvent.Reason.UPDATE_DISPLAY_NAME)
                .build());

        return commonRank;
    }

    public Rank updateRankPriority(final String rankId, final int newPriority) {
        final RankObject rank = this.findRankObjectByIdOrThrow(rankId);

        if (!RankCommon.isValidPriority(newPriority)) {
            throw new RankUpdateException(rankId, RankUpdateException.Reason.INVALID_PRIORITY);
        }

        final RankObject saved = this.rankRepository.updateRankPriority(rank.getRankId(), newPriority);

        final Rank commonRank = convertToCommonRank(saved);

        eventPublisher.publishRankUpdate(RankUpdateEvent.builder()
                .rank(commonRank)
                .reason(RankUpdateEvent.Reason.UPDATE_PRIORITY)
                .build());

        return commonRank;
    }

    public Rank addRankPermissions(final String rankId, final Set<String> newPermissions) {
        final RankObject rank = this.findRankObjectByIdOrThrow(rankId);
        final RankObject saved = this.rankRepository.addRankPermissions(rank.getRankId(), newPermissions);

        final Rank commonRank = convertToCommonRank(saved);

        eventPublisher.publishRankPermissionUpdate(RankPermissionUpdateEvent.builder()
                        .updatedRank(commonRank)
                        .updatedAffectedRanks(this.getRanksInheritingRank(rankId))
                        .reason(RankPermissionUpdateEvent.Reason.ADD_PERMISSION)
                .build());

        return commonRank;
    }

    public Rank removeRankPermissions(final String rankId, final Set<String> permissionsToRemove) {
        final RankObject rank = this.findRankObjectByIdOrThrow(rankId);
        final RankObject saved =
                this.rankRepository.removeRankPermissions(rank.getRankId(), permissionsToRemove);

        final Rank commonRank = convertToCommonRank(saved);

        eventPublisher.publishRankPermissionUpdate(RankPermissionUpdateEvent.builder()
                .updatedRank(commonRank)
                .updatedAffectedRanks(this.getRanksInheritingRank(rankId))
                .reason(RankPermissionUpdateEvent.Reason.REMOVE_PERMISSION)
                .build());

        return commonRank;
    }

    public Rank addRankInheritance(final String rankId, final Set<String> inheritedRankIds) {
        final RankObject rank = this.findRankObjectByIdOrThrow(rankId);
        final RankObject saved =
                this.rankRepository.addRankInheritedRankIds(rank.getRankId(), inheritedRankIds);

        final Rank commonRank = convertToCommonRank(saved);

        eventPublisher.publishRankInheritanceUpdate(RankInheritanceUpdateEvent.builder()
                        .updatedRank(commonRank)
                        .updatedAffectedRanks(this.getRanksInheritingRank(rankId))
                        .reason(RankInheritanceUpdateEvent.Reason.ADD_INHERITANCE)
                .build());

        return commonRank;
    }

    public Rank removeRankInheritance(final String rankId, final Set<String> inheritedRankIds) {
        final RankObject rank = this.findRankObjectByIdOrThrow(rankId);
        final RankObject saved =
                this.rankRepository.removeRankInheritedRankIds(rank.getRankId(), inheritedRankIds);

        final Rank commonRank = convertToCommonRank(saved);

        eventPublisher.publishRankInheritanceUpdate(RankInheritanceUpdateEvent.builder()
                .updatedRank(commonRank)
                .updatedAffectedRanks(this.getRanksInheritingRank(rankId))
                .reason(RankInheritanceUpdateEvent.Reason.REMOVE_INHERITANCE)
                .build());

        return commonRank;
    }

    public boolean deleteRank(final String rankId) {
        final List<Rank> affectedRanks =
                this.getRanksInheritingRank(rankId);

        // Clear rank assignments for players with this rank
        this.playerRankAssignmentRepository
                .removePlayerRankAssignments(rankId);

        // Remove inheritance from other ranks
        this.rankRepository
                .removeInheritedRankIdFromRanksByQuery(rankId);

        // Delete the actual rank
        final RankObject deletedRankObject =
                this.rankRepository.deleteRank(rankId);

        if (deletedRankObject != null) {
            final Rank deletedRank = convertToCommonRank(deletedRankObject);

            eventPublisher.publishRankDelete(
                    RankDeleteEvent.builder()
                            .deletedRank(deletedRank)
                            .updatedAffectedRanks(affectedRanks)
                            .build());

            return true;
        }

        return false;
    }

    public PlayerRankAssignment assignPlayerRank(
            final String playerId, @Nullable final String assignedById, final String rankId) {
        final RankObject rank = this.findRankObjectByIdOrThrow(rankId);
        final PlayerRankAssignmentObject rankAssignment = this.playerRankAssignmentRepository.setPlayerRankAssignment(
                playerId, rank, assignedById, Instant.now());
        final Rank dtoRank = this.getRankById(rankAssignment.getRankId());

        final PlayerRankAssignment commonPlayerRankAssignment = this.convertToCommonAssignment(rankAssignment, dtoRank);

        eventPublisher.publishPlayerRankAssign(PlayerRankAssignEvent.builder()
                .playerRankAssignment(commonPlayerRankAssignment)
                .build());

        return commonPlayerRankAssignment;
    }

    public List<PlayerRankAssignment> getPlayerRankAssignments(final String playerId) {
        return this.playerRankAssignmentRepository.findAllByPlayerId(playerId).stream()
                .map(playerRankAssignmentObject -> {
                    Rank dtoRank = this.getRankById(playerRankAssignmentObject.getRankId());
                    return this.convertToCommonAssignment(playerRankAssignmentObject, dtoRank);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public List<Rank> getPlayerRanks(final String playerId) {
        return this.playerRankAssignmentRepository.findAllByPlayerId(playerId).stream()
                .map(playerRankAssignmentObject -> {
                    RankObject rankObject = this.findRankObjectByIdOrThrow(
                            playerRankAssignmentObject.getRankId());
                    return this.convertToCommonRank(rankObject);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public List<PlayerRankAssignment> getPlayerRankAssignmentsWithRank(final String rankId) {
        return this.playerRankAssignmentRepository.findAllByRankId(rankId).stream()
                .map(playerRankAssignmentObject -> {
                    Rank dtoRank = this.getRankById(playerRankAssignmentObject.getRankId());
                    return this.convertToCommonAssignment(playerRankAssignmentObject, dtoRank);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public boolean removePlayerRank(final String playerId) {
        final PlayerRankAssignmentObject rankAssignmentObject =
                this.findPlayerRankAssignmentObjectOrThrow(playerId);

        final Rank commonRank = this.getRankById(rankAssignmentObject.getRankId());
        final PlayerRankAssignment commonPlayerRankAssignment = this.convertToCommonAssignment(rankAssignmentObject, commonRank);

        final boolean removed =
                this.playerRankAssignmentRepository.removePlayerRankAssignment(
                        rankAssignmentObject.getPlayerId()
                );

        if (removed) {
            eventPublisher.publishPlayerRankRemove(
                    PlayerRankRemoveEvent.builder()
                            .playerRankAssignment(commonPlayerRankAssignment)
                            .build()
            );
        }

        return removed;
    }

    public List<Rank> getRanksByIds(final Set<String> rankIds) {
        return this.rankRepository.findAllByRankIdIn(rankIds).stream()
                .map(this::convertToCommonRank)
                .collect(Collectors.toList());
    }

    public List<PlayerRankAssignment> getPlayerRankAssignmentsByIds(final Set<String> playerIds) {
        final List<PlayerRankAssignmentObject> playerRankAssignmentObjects =
                this.playerRankAssignmentRepository.findAllByPlayerIdIn(playerIds);
        final List<PlayerRankAssignment> playerRankAssignments = new ArrayList<>();
        playerRankAssignmentObjects.forEach(playerRankAssignment -> {
            final Rank rank = this.getRankById(playerRankAssignment.getRankId());
            playerRankAssignments.add(this.convertToCommonAssignment(playerRankAssignment, rank));
        });

        return playerRankAssignments;
    }

    public List<Rank> getRanksInheritingRank(final String rankId) {
        final Set<String> visited = new HashSet<>();
        final Queue<String> queue = new LinkedList<>();
        final List<RankObject> result = new ArrayList<>();

        queue.add(rankId);

        while (!queue.isEmpty()) {
            final String currentRankId = queue.poll();
            final List<RankObject> inheritingRanks =
                    this.rankRepository.findAllByInheritedRankIdsContains(currentRankId);

            for (final RankObject rankObj : inheritingRanks) {
                if (visited.add(rankObj.getRankId())) {
                    result.add(rankObj);
                    queue.add(rankObj.getRankId());
                }
            }
        }

        return result.stream().map(this::convertToCommonRank).toList();
    }

    private RankObject findRankObjectByIdOrThrow(@NonNull final String rankId) {
        final Optional<RankObject> rankOptional = this.rankRepository.findByRankId(rankId);
        if (rankOptional.isEmpty()) {
            throw new RankIdNotFoundException(rankId);
        }
        return rankOptional.get();
    }

    private PlayerRankAssignmentObject findPlayerRankAssignmentObjectOrThrow(@NonNull final String playerId) {
        final Optional<PlayerRankAssignmentObject> rankAssignmentOptional =
                this.playerRankAssignmentRepository.findByPlayerId(playerId);
        if (rankAssignmentOptional.isEmpty()) {
            throw new RankNotAssignedException(playerId);
        }
        return rankAssignmentOptional.get();
    }
}
