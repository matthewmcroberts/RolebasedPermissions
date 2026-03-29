package com.matthewmcroberts.rankmanager.repository.model;

import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Builder(toBuilder = true)
@Value
@Document("Rank")
public class RankObject {
    @MongoId
    String id;

    @NonNull String rankId;

    @NonNull String displayName;

    @Builder.Default
    int priority = 1_000;

    @Indexed
    @Builder.Default
    @NonNull Set<String> effectivePermissions = new HashSet<>();

    @Indexed
    @Builder.Default
    @NonNull Set<String> ownPermissions = new HashSet<>();

    @Indexed
    @Builder.Default
    @NonNull Set<String> inheritedRankIds = new HashSet<>();
}
