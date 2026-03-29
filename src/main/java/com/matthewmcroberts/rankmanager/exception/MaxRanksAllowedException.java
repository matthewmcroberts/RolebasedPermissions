package com.matthewmcroberts.rankmanager.exception;

import java.io.Serial;
import lombok.Getter;

@Getter
public class MaxRanksAllowedException extends RankException {
    @Serial
    private static final long serialVersionUID = -9006919448294976737L;

    private final int maxRanksAllowed;

    public MaxRanksAllowedException(int maxRanksAllowed) {
        super("Maximum number of allowed ranks reached: " + maxRanksAllowed);
        this.maxRanksAllowed = maxRanksAllowed;
    }
}
