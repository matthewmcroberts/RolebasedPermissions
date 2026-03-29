package com.matthewmcroberts.rankmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Request body for {@code POST /v1/ranks}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewRank {
    private String rankId;
    private String displayName;
    private int priority;
    private Set<String> ownPermissions;
    private Set<String> effectivePermissions;
    private Set<String> inheritedRankIds;
}
