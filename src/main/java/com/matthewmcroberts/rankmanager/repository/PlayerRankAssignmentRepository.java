package com.matthewmcroberts.rankmanager.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.matthewmcroberts.rankmanager.repository.model.PlayerRankAssignmentObject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRankAssignmentRepository
        extends MongoRepository<PlayerRankAssignmentObject, String>, CustomRankAssignmentRepository {
    List<PlayerRankAssignmentObject> findAllByRankId(String rankId);

    Optional<PlayerRankAssignmentObject> findByPlayerId(String playerId);

    List<PlayerRankAssignmentObject> findAllByPlayerId(
            String playerId);

    List<PlayerRankAssignmentObject> findAllByPlayerIdIn(
            Collection<String> playerIds);
}
