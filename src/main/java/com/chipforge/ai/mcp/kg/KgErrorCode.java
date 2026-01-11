package com.chipforge.ai.mcp.kg;

/**
 * Knowledge graph error codes.
 */
public enum KgErrorCode {
    NODE_EXISTS("Node already exists"),
    NODE_NOT_FOUND("Node not found"),
    RELATION_EXISTS("Relation already exists"),
    RELATION_NOT_FOUND("Relation not found"),
    HAS_RELATIONS("Node has relations, cannot delete"),
    INVALID_ID("Invalid node ID format"),
    INVALID_TYPE("Invalid node or relation type");

    private final String message;

    KgErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
