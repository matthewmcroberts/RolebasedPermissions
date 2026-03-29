package com.matthewmcroberts.rankmanager.repository;

import com.matthewmcroberts.rankmanager.repository.model.RankObject;

import java.util.List;
import java.util.Set;

public interface CustomRankRepository {

    RankObject updateRankDisplayName(String rankId, String displayName);

    RankObject updateRankPriority(String rankId, int priority);

    RankObject addRankPermissions(String rankId, Set<String> permission);

    RankObject addRankInheritedRankIds(String rankId, Set<String> inheritedRankIds);

    RankObject removeRankPermissions(String rankId, Set<String> permission);

    RankObject removeRankInheritedRankIds(String rankId, Set<String> inheritedRankIds);

    List<RankObject> removeInheritedRankIdFromRanksByQuery(String inheritedRankId);

    RankObject deleteRank(String rankId);
}
