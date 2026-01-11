package com.chipforge.ai.config;

import com.chipforge.ai.mcp.kg.KgTools;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.WebMvcSseServerTransportProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * MCP Server configuration.
 */
@Configuration
public class McpConfig {

    private static final String MESSAGE_ENDPOINT = "/mcp/message";

    @Bean
    public WebMvcSseServerTransportProvider webMvcSseServerTransportProvider(ObjectMapper objectMapper) {
        return new WebMvcSseServerTransportProvider(objectMapper, MESSAGE_ENDPOINT);
    }

    @Bean
    public RouterFunction<ServerResponse> mcpRouterFunction(WebMvcSseServerTransportProvider transportProvider) {
        return transportProvider.getRouterFunction();
    }

    @Bean
    public McpSyncServer mcpSyncServer(WebMvcSseServerTransportProvider transportProvider, KgTools kgTools) {
        var builder = McpServer.sync(transportProvider)
            .serverInfo("chipforge-ai", "0.1.0");

        // Register all knowledge graph tools
        for (var spec : kgTools.getAllToolSpecifications()) {
            builder = builder.tool(spec.tool(), spec.call());
        }

        return builder.build();
    }
}
