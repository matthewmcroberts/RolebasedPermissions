package com.matthewmcroberts.rankmanager.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.*;

import com.matthewmcroberts.rankmanager.RankCommon;
import com.matthewmcroberts.rankmanager.exception.RankIdNotFoundException;
import com.matthewmcroberts.rankmanager.repository.model.RankObject;
import com.matthewmcroberts.rankmanager.repository.model.SanatizedPermissionNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomRankRepositoryImpl implements CustomRankRepository {
    private static final String FIELD_RANK_ID = "rankId";
    private static final String FIELD_DISPLAY_NAME = "displayName";
    private static final String FIELD_PRIORITY = "priority";
    private static final String FIELD_PERMISSIONS = "effectivePermissions";
    private static final String FIELD_OWN_PERMISSIONS = "ownPermissions";
    private static final String FIELD_INHERITED_RANK_IDS = "inheritedRankIds";

    private final MongoTemplate mongoTemplate;

    @Override
    public RankObject updateRankDisplayName(final String rankId, final String displayName) {
        final Query query = new Query(Criteria.where(FIELD_RANK_ID).is(rankId));
        final Update update = new Update().set(FIELD_DISPLAY_NAME, displayName);
        return this.mongoTemplate.findAndModify(
                query, update, FindAndModifyOptions.options().returnNew(true), RankObject.class);
    }

    @Override
    public RankObject updateRankPriority(final String rankId, final int priority) {
        final Query query = new Query(Criteria.where(FIELD_RANK_ID).is(rankId));
        final Update update = new Update().set(FIELD_PRIORITY, priority);
        return this.mongoTemplate.findAndModify(
                query, update, FindAndModifyOptions.options().returnNew(true), RankObject.class);
    }

    @Override
    public RankObject addRankPermissions(final String rankId, final Set<String> permission) {
        final Query query = new Query(Criteria.where(FIELD_RANK_ID).is(rankId));
        final Update update = new Update().addToSet(FIELD_OWN_PERMISSIONS).each(permission.toArray());
        final RankObject updated = mongoTemplate.findAndModify(
                query, update, FindAndModifyOptions.options().returnNew(true), RankObject.class);
        if (updated == null) {
            throw new RankIdNotFoundException(rankId);
        }

        this.recomputeEffectivePermissionsAndPropagate(rankId);
        return this.mongoTemplate.findOne(query, RankObject.class);
    }

    @Override
    public RankObject removeRankPermissions(final String rankId, final Set<String> permission) {
        final Query query = new Query(Criteria.where(FIELD_RANK_ID).is(rankId));
        final Update update = new Update().pullAll(FIELD_OWN_PERMISSIONS, permission.toArray());
        final RankObject updated = mongoTemplate.findAndModify(
                query, update, FindAndModifyOptions.options().returnNew(true), RankObject.class);
        if (updated == null) {
            throw new RankIdNotFoundException(rankId);
        }

        this.recomputeEffectivePermissionsAndPropagate(rankId);
        return this.mongoTemplate.findOne(query, RankObject.class);
    }

    @Override
    public RankObject addRankInheritedRankIds(final String rankId, final Set<String> inheritedRankIds) {
        final Query query = new Query(Criteria.where(FIELD_RANK_ID).is(rankId));
        final Update update = new Update().addToSet(FIELD_INHERITED_RANK_IDS).each(inheritedRankIds.toArray());
        final RankObject updated = mongoTemplate.findAndModify(
                query, update, FindAndModifyOptions.options().returnNew(true), RankObject.class);
        if (updated == null) {
            throw new RankIdNotFoundException(rankId);
        }

        this.recomputeEffectivePermissionsAndPropagate(rankId);
        return this.mongoTemplate.findOne(query, RankObject.class);
    }

    @Override
    public RankObject removeRankInheritedRankIds(final String rankId, final Set<String> inheritedRankIds) {
        final Query query = new Query(Criteria.where(FIELD_RANK_ID).is(rankId));

        final RankObject current = mongoTemplate.findOne(query, RankObject.class);
        if (current == null) {
            throw new RankIdNotFoundException(rankId);
        }

        final Update update = new Update().pullAll(FIELD_INHERITED_RANK_IDS, inheritedRankIds.toArray());
        final RankObject updated = mongoTemplate.findAndModify(
                query, update, FindAndModifyOptions.options().returnNew(true), RankObject.class);
        if (updated == null) {
            throw new RankIdNotFoundException(rankId);
        }

        this.recomputeEffectivePermissionsAndPropagate(rankId);
        return this.mongoTemplate.findOne(query, RankObject.class);
    }

    @Override
    public List<RankObject> removeInheritedRankIdFromRanksByQuery(final String inheritedRankId) {
        final Query findQuery = new Query(Criteria.where(FIELD_INHERITED_RANK_IDS).is(inheritedRankId));
        final List<RankObject> affected = this.mongoTemplate.find(findQuery, RankObject.class);

        if (affected.isEmpty()) {
            return Collections.emptyList();
        }

        final Update update = new Update().pull(FIELD_INHERITED_RANK_IDS, inheritedRankId);
        this.mongoTemplate.updateMulti(findQuery, update, RankObject.class);

        final List<RankObject> updatedList = new ArrayList<>(affected.size());
        for (final RankObject rankObject : affected) {
            this.recomputeEffectivePermissionsAndPropagate(rankObject.getRankId());
            final Query q = new Query(Criteria.where(FIELD_RANK_ID).is(rankObject.getRankId()));
            final RankObject refreshed = this.mongoTemplate.findOne(q, RankObject.class);
            if (refreshed != null) {
                updatedList.add(refreshed);
            }
        }
        return updatedList;
    }

    @Override
    public RankObject deleteRank(final String rankId) {
        final Query query = Query.query(where(FIELD_RANK_ID).is(rankId));
        final RankObject rank = this.mongoTemplate.findOne(query, RankObject.class);
        if (rank == null) {
            throw new RankIdNotFoundException(rankId);
        }

        this.mongoTemplate.remove(query, RankObject.class);

        final Query parentQuery = new Query(Criteria.where(FIELD_INHERITED_RANK_IDS).is(rankId));
        final Update pullUpdate = new Update().pull(FIELD_INHERITED_RANK_IDS, rankId);
        this.mongoTemplate.updateMulti(parentQuery, pullUpdate, RankObject.class);

        final List<RankObject> parents = this.mongoTemplate.find(parentQuery, RankObject.class);
        for (final RankObject parent : parents) {
            this.recomputeEffectivePermissionsAndPropagate(parent.getRankId());
        }

        return rank;
    }

    private void recomputeEffectivePermissionsAndPropagate(final String rankId) {
        final Set<String> processed = new HashSet<>();
        final Queue<String> queue = new LinkedList<>();
        queue.offer(rankId);

        while (!queue.isEmpty()) {
            final String currentRankId = queue.poll();
            if (!processed.add(currentRankId)) {
                continue;
            }

            final Set<String> recomputed = computeEffectivePermissions(currentRankId, new HashSet<>());
            final Query q = new Query(Criteria.where(FIELD_RANK_ID).is(currentRankId));
            final Update updateEffective = new Update().set(FIELD_PERMISSIONS, recomputed);
            this.mongoTemplate.updateFirst(q, updateEffective, RankObject.class);

            final Query parentQuery = new Query(Criteria.where(FIELD_INHERITED_RANK_IDS).in(currentRankId));
            final List<RankObject> parents = mongoTemplate.find(parentQuery, RankObject.class);
            for (final RankObject parent : parents) {
                if (!processed.contains(parent.getRankId())) {
                    queue.offer(parent.getRankId());
                }
            }
        }
    }

    private Set<String> computeEffectivePermissions(final String rankId, final Set<String> visited) {
        if (!visited.add(rankId)) {
            return Set.of();
        }

        final Query q = new Query(Criteria.where(FIELD_RANK_ID).is(rankId));
        final RankObject rank = mongoTemplate.findOne(q, RankObject.class);
        if (rank == null) {
            return Set.of();
        }

        final Set<String> own = Optional.ofNullable(rank.getOwnPermissions()).orElse(Set.of());
        final Set<String> inherited = new HashSet<>();

        final Set<String> inheritedIds = Optional.ofNullable(rank.getInheritedRankIds()).orElse(Set.of());
        for (String inheritedId : inheritedIds) {
            inherited.addAll(computeEffectivePermissions(inheritedId, visited));
        }

        final Set<String> effective = new HashSet<>(inherited);

        for (String ownPermission : own) {
            final SanatizedPermissionNode sanitized = RankCommon.parsePermissionNode(ownPermission);
            effective.removeIf(inheritedPerm -> {
                final SanatizedPermissionNode inheritedSanitized = RankCommon.parsePermissionNode(inheritedPerm);
                return inheritedSanitized.getSanitizedNode().equals(sanitized.getSanitizedNode());
            });

            effective.add(ownPermission);
        }

        log.info("[new] Computed effective permissions for rankId={}: {}", rankId, effective);

        return effective;
    }
}
