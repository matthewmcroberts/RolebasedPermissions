package com.matthewmcroberts.rankmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for {@code PATCH /v1/ranks/{id}/display-name}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayNameUpdate {
    private String displayName;
}
