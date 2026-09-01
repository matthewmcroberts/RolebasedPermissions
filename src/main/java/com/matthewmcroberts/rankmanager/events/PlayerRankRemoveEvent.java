package com.matthewmcroberts.rankmanager.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.matthewmcroberts.rankmanager.dto.PlayerRankAssignment;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerRankRemoveEvent {
    @NonNull
    PlayerRankAssignment playerRankAssignment;
}
