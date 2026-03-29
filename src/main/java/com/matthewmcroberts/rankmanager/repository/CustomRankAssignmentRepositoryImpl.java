package com.matthewmcroberts.rankmanager.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.time.Instant;
import java.util.List;

import com.matthewmcroberts.rankmanager.repository.model.PlayerRankAssignmentObject;
import com.matthewmcroberts.rankmanager.repository.model.RankObject;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomRankAssignmentRepositoryImpl implements CustomRankAssignmentRepository {
    private static final String FIELD_PLAYER_ID = "playerId";
    private static final String FIELD_ASSIGNED_BY_ID = "assignedById";
    private static final String FIELD_ASSIGNED_AT = "assignedAtTimestamp";
    private static final String FIELD_RANK_ID = "rankId";

    private final MongoTemplate mongoTemplate;

    @Override
    public PlayerRankAssignmentObject setPlayerRankAssignment(
            final String playerId,
            final RankObject rank,
            @Nullable final String assignedById,
            final Instant assignedAtTimestamp) {
        Query query = new Query(Criteria.where(FIELD_PLAYER_ID)
                .is(playerId));
        Update update = new Update()
                .set(FIELD_PLAYER_ID, playerId)
                .set(FIELD_RANK_ID, rank.getRankId())
                .set(FIELD_ASSIGNED_BY_ID, assignedById)
                .set(FIELD_ASSIGNED_AT, assignedAtTimestamp);

        return mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                PlayerRankAssignmentObject.class);
    }

    @Override
    public boolean removePlayerRankAssignment(String playerId) {
        final Query query = Query.query(
                where(FIELD_PLAYER_ID).is(playerId));
        return this.mongoTemplate.findAndRemove(query, PlayerRankAssignmentObject.class) != null;
    }

    @Override
    public List<PlayerRankAssignmentObject> removePlayerRankAssignments(String rankId) {
        final Query query = Query.query(
                where(FIELD_RANK_ID).is(rankId));

        final List<PlayerRankAssignmentObject> toRemove =
                this.mongoTemplate.find(query, PlayerRankAssignmentObject.class);
        this.mongoTemplate.remove(query, PlayerRankAssignmentObject.class);

        return toRemove;
    }
}
