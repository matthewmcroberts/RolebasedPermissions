package com.matthewmcroberts.rankmanager;

import java.util.regex.Pattern;

import com.matthewmcroberts.rankmanager.repository.model.SanatizedPermissionNode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankCommon {
    private static final Pattern RANK_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9 <>:/#\\-_\\[\\]]{1,64}$");

    public static final char NEGATED_PERMISSION_NODE_PREFIX = '-';

    public static boolean isValidRankName(@NonNull final String name) {
        return RANK_NAME_PATTERN.matcher(name).matches()
                && !name.trim().isEmpty(); // Ensure the name is not just whitespace
    }

    public static boolean isValidPriority(final int priority) {
        return priority >= 0;
    }

    public static SanatizedPermissionNode parsePermissionNode(final String node) {
        final boolean negated = node.charAt(0) == NEGATED_PERMISSION_NODE_PREFIX;
        final String sanitizedNode = negated ? node.substring(1) : node;
        return SanatizedPermissionNode.builder()
                .node(node)
                .sanitizedNode(sanitizedNode)
                .negated(negated)
                .build();
    }
}
