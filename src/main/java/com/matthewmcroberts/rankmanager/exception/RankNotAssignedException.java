package com.matthewmcroberts.rankmanager.exception;

import java.io.Serial;
import lombok.Getter;

@Getter
public class RankNotAssignedException extends RankException {
    @Serial
    private static final long serialVersionUID = -7388667790220957070L;

    private final String playerId;

    public RankNotAssignedException(final String playerId) {
        super("No rank assigned to player with id: " + playerId);
        this.playerId = playerId;
    }
}
