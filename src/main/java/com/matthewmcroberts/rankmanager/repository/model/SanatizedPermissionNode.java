package com.matthewmcroberts.rankmanager.repository.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class SanatizedPermissionNode {
    @NonNull String node;

    @NonNull String sanitizedNode;

    boolean negated;
}
