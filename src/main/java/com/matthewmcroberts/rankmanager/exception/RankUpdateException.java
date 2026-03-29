package com.matthewmcroberts.rankmanager.exception;

import java.io.Serial;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class RankUpdateException extends RankException {
    @Serial
    private static final long serialVersionUID = 3499409176541926630L;

    @NonNull private final String rankId;

    @NonNull private final Reason reason;

    public RankUpdateException(final String rankId, final Reason reason) {
        super("Failed to update rank with id: " + rankId + " due to: "
                + reason.name());

        this.rankId = rankId;
        this.reason = reason;
    }

    public enum Reason {
        INVALID_NAME,
        INVALID_PRIORITY
    }
}
