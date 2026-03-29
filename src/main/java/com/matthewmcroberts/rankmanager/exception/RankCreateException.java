package com.matthewmcroberts.rankmanager.exception;

import java.io.Serial;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class RankCreateException extends RankException {
    @Serial
    private static final long serialVersionUID = 6968048514886499677L;

    @NonNull private final String rankId;

    @NonNull private final String displayName;

    private final int priority;

    @NonNull private final Reason reason;

    public RankCreateException(
            @NonNull final String rankId,
            @NonNull final String displayName,
            final int priority,
            @NonNull final Reason reason) {
        super("Failed to create rank: " + reason.name());
        this.rankId = rankId;
        this.displayName = displayName;
        this.priority = priority;
        this.reason = reason;
    }

    public enum Reason {
        INVALID_NAME,
        ID_ALREADY_EXISTS,
        INVALID_PRIORITY
    }
}
