package com.chipforge.ai.mcp.kg;

/**
 * Knowledge graph relation.
 */
public record KgRelation(
    String from,
    String to,
    String type
) {
    public static boolean isValidType(String type) {
        return type != null && (
            type.equals("EXAMPLES") ||
            type.equals("STATETRANSITION") ||
            type.equals("RELATED")
        );
    }
}
