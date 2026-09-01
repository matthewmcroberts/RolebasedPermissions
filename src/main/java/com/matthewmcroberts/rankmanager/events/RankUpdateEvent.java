package com.matthewmcroberts.rankmanager.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.matthewmcroberts.rankmanager.dto.Rank;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class RankUpdateEvent {
    @NonNull
    Rank rank;

    @NonNull Reason reason;

    public enum Reason {
        UPDATE_DISPLAY_NAME,
        UPDATE_PRIORITY
    }
}
