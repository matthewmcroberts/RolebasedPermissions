package com.matthewmcroberts.rankmanager.exception;

import java.io.Serial;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class RankIdNotFoundException extends RankException {
    @Serial
    private static final long serialVersionUID = -9011692014841355982L;

    @NonNull private final String rankId;

    public RankIdNotFoundException(final String rankId) {
        super("No rank was found for id: " + rankId);
        this.rankId = rankId;
    }
}
