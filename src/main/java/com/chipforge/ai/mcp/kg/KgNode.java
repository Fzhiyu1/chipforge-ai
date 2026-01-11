package com.chipforge.ai.mcp.kg;

import java.util.Map;

/**
 * Knowledge graph node.
 */
public record KgNode(
    String id,
    String type,
    Map<String, Object> properties
) {
    public static final String ID_PATTERN = "^[a-zA-Z_][a-zA-Z0-9_-]{0,59}$";

    public static boolean isValidId(String id) {
        return id != null && id.matches(ID_PATTERN);
    }

    public static boolean isValidType(String type) {
        return type != null && (
            type.equals("Signal") ||
            type.equals("StateTransition") ||
            type.equals("SignalExample")
        );
    }
}
