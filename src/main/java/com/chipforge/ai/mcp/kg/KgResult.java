package com.chipforge.ai.mcp.kg;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Generic result wrapper for knowledge graph operations.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record KgResult<T>(
    boolean success,
    T data,
    String error,
    String code
) {
    public static <T> KgResult<T> ok(T data) {
        return new KgResult<>(true, data, null, null);
    }

    public static <T> KgResult<T> ok() {
        return new KgResult<>(true, null, null, null);
    }

    public static <T> KgResult<T> failure(KgErrorCode errorCode) {
        return new KgResult<>(false, null, errorCode.getMessage(), errorCode.name());
    }
}
