package com.matthewmcroberts.rankmanager.repository.model;

import com.mongodb.lang.Nullable;
import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Builder(toBuilder = true)
@Value
@Document("PlayerRankAssignment")
public class PlayerRankAssignmentObject {
    @MongoId
    String id;

    @NonNull
    @Indexed(unique = true)
    String playerId;

    @NonNull String rankId;

    @Nullable String assignedById;

    @NonNull Instant assignedAtTimestamp;
}
