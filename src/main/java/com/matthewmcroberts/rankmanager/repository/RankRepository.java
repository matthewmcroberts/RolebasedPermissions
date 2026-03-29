package com.matthewmcroberts.rankmanager.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.matthewmcroberts.rankmanager.repository.model.RankObject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankRepository extends MongoRepository<RankObject, String>, CustomRankRepository {
    boolean existsByRankId(String displayName);

    Optional<RankObject> findByDisplayName(String displayName);

    Optional<RankObject> findByRankId(String rankId);

    List<RankObject> findAllByRankIdIn(Collection<String> rankIds);

    List<RankObject> findAll();

    List<RankObject> findAllByInheritedRankIdsContains(String inheritedRankId);
}
