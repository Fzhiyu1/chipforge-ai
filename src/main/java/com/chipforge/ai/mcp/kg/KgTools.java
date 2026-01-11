package com.chipforge.ai.mcp.kg;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.JsonSchema;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MCP tool definitions for knowledge graph operations.
 */
@Component
public class KgTools {

    private final KgService kgService;
    private final ObjectMapper objectMapper;

    public KgTools(KgService kgService, ObjectMapper objectMapper) {
        this.kgService = kgService;
        this.objectMapper = objectMapper;
    }

    public List<SyncToolSpecification> getAllToolSpecifications() {
        List<SyncToolSpecification> specs = new ArrayList<>();
        specs.add(createKgInit());
        specs.add(createKgCreateNode());
        specs.add(createKgCreateRelation());
        specs.add(createKgDeleteNode());
        specs.add(createKgDeleteRelation());
        specs.add(createKgUpdateNode());
        specs.add(createKgQuery());
        specs.add(createKgList());
        return specs;
    }

    private Tool buildTool(String name, String description, JsonSchema inputSchema) {
        return new Tool(name, null, description, inputSchema, null, null, null);
    }

    private JsonSchema buildSchema(Map<String, Object> properties, List<String> required) {
        return new JsonSchema("object", properties, required, null, null, null);
    }

    private SyncToolSpecification createKgInit() {
        JsonSchema schema = buildSchema(
            Map.of(
                "graphId", Map.of("type", "string", "description", "Graph identifier"),
                "nodes", Map.of("type", "string", "description", "JSON array of nodes"),
                "relations", Map.of("type", "string", "description", "JSON array of relations")
            ),
            List.of("graphId", "nodes", "relations")
        );
        Tool tool = buildTool("kg_init", "Initialize knowledge graph with nodes and relations", schema);

        return SyncToolSpecification.builder()
            .tool(tool)
            .callHandler((exchange, request) -> {
                try {
                    Map<String, Object> args = request.arguments();
                    String graphId = (String) args.get("graphId");
                    String nodesJson = (String) args.get("nodes");
                    String relationsJson = (String) args.get("relations");

                    List<KgNode> nodes = objectMapper.readValue(nodesJson,
                        new TypeReference<List<KgNode>>() {});
                    List<KgRelation> relations = objectMapper.readValue(relationsJson,
                        new TypeReference<List<KgRelation>>() {});

                    KgResult<Void> result = kgService.init(graphId, nodes, relations);
                    return toCallToolResult(result);
                } catch (Exception e) {
                    return errorResult(e.getMessage());
                }
            })
            .build();
    }

    private SyncToolSpecification createKgCreateNode() {
        JsonSchema schema = buildSchema(
            Map.of(
                "graphId", Map.of("type", "string"),
                "id", Map.of("type", "string"),
                "type", Map.of("type", "string", "enum", List.of("Signal", "StateTransition", "SignalExample")),
                "properties", Map.of("type", "string", "description", "JSON object of properties")
            ),
            List.of("graphId", "id", "type")
        );
        Tool tool = buildTool("kg_create_node", "Create a new node in the knowledge graph", schema);

        return SyncToolSpecification.builder()
            .tool(tool)
            .callHandler((exchange, request) -> {
                try {
                    Map<String, Object> args = request.arguments();
                    String graphId = (String) args.get("graphId");
                    String id = (String) args.get("id");
                    String type = (String) args.get("type");
                    String propsJson = (String) args.get("properties");

                    Map<String, Object> props = null;
                    if (propsJson != null && !propsJson.isEmpty()) {
                        props = objectMapper.readValue(propsJson, new TypeReference<>() {});
                    }

                    KgResult<KgNode> result = kgService.createNode(graphId, id, type, props);
                    return toCallToolResult(result);
                } catch (Exception e) {
                    return errorResult(e.getMessage());
                }
            })
            .build();
    }

    private SyncToolSpecification createKgCreateRelation() {
        JsonSchema schema = buildSchema(
            Map.of(
                "graphId", Map.of("type", "string"),
                "fromId", Map.of("type", "string"),
                "toId", Map.of("type", "string"),
                "type", Map.of("type", "string", "enum", List.of("EXAMPLES", "STATETRANSITION", "RELATED"))
            ),
            List.of("graphId", "fromId", "toId", "type")
        );
        Tool tool = buildTool("kg_create_relation", "Create a relation between two nodes", schema);

        return SyncToolSpecification.builder()
            .tool(tool)
            .callHandler((exchange, request) -> {
                try {
                    Map<String, Object> args = request.arguments();
                    String graphId = (String) args.get("graphId");
                    String fromId = (String) args.get("fromId");
                    String toId = (String) args.get("toId");
                    String type = (String) args.get("type");

                    KgResult<KgRelation> result = kgService.createRelation(graphId, fromId, toId, type);
                    return toCallToolResult(result);
                } catch (Exception e) {
                    return errorResult(e.getMessage());
                }
            })
            .build();
    }

    private SyncToolSpecification createKgDeleteNode() {
        JsonSchema schema = buildSchema(
            Map.of(
                "graphId", Map.of("type", "string"),
                "id", Map.of("type", "string")
            ),
            List.of("graphId", "id")
        );
        Tool tool = buildTool("kg_delete_node", "Delete a node from the knowledge graph", schema);

        return SyncToolSpecification.builder()
            .tool(tool)
            .callHandler((exchange, request) -> {
                try {
                    Map<String, Object> args = request.arguments();
                    String graphId = (String) args.get("graphId");
                    String id = (String) args.get("id");

                    KgResult<Void> result = kgService.deleteNode(graphId, id);
                    return toCallToolResult(result);
                } catch (Exception e) {
                    return errorResult(e.getMessage());
                }
            })
            .build();
    }

    private SyncToolSpecification createKgDeleteRelation() {
        JsonSchema schema = buildSchema(
            Map.of(
                "graphId", Map.of("type", "string"),
                "fromId", Map.of("type", "string"),
                "toId", Map.of("type", "string"),
                "type", Map.of("type", "string")
            ),
            List.of("graphId", "fromId", "toId", "type")
        );
        Tool tool = buildTool("kg_delete_relation", "Delete a relation from the knowledge graph", schema);

        return SyncToolSpecification.builder()
            .tool(tool)
            .callHandler((exchange, request) -> {
                try {
                    Map<String, Object> args = request.arguments();
                    String graphId = (String) args.get("graphId");
                    String fromId = (String) args.get("fromId");
                    String toId = (String) args.get("toId");
                    String type = (String) args.get("type");

                    KgResult<Void> result = kgService.deleteRelation(graphId, fromId, toId, type);
                    return toCallToolResult(result);
                } catch (Exception e) {
                    return errorResult(e.getMessage());
                }
            })
            .build();
    }

    private SyncToolSpecification createKgUpdateNode() {
        JsonSchema schema = buildSchema(
            Map.of(
                "graphId", Map.of("type", "string"),
                "id", Map.of("type", "string"),
                "properties", Map.of("type", "string", "description", "JSON object of properties")
            ),
            List.of("graphId", "id", "properties")
        );
        Tool tool = buildTool("kg_update_node", "Update node properties", schema);

        return SyncToolSpecification.builder()
            .tool(tool)
            .callHandler((exchange, request) -> {
                try {
                    Map<String, Object> args = request.arguments();
                    String graphId = (String) args.get("graphId");
                    String id = (String) args.get("id");
                    String propsJson = (String) args.get("properties");

                    Map<String, Object> props = objectMapper.readValue(propsJson, new TypeReference<>() {});
                    KgResult<KgNode> result = kgService.updateNode(graphId, id, props);
                    return toCallToolResult(result);
                } catch (Exception e) {
                    return errorResult(e.getMessage());
                }
            })
            .build();
    }

    private SyncToolSpecification createKgQuery() {
        JsonSchema schema = buildSchema(
            Map.of(
                "graphId", Map.of("type", "string"),
                "nodeId", Map.of("type", "string"),
                "depth", Map.of("type", "integer", "default", 1)
            ),
            List.of("graphId", "nodeId")
        );
        Tool tool = buildTool("kg_query", "BFS query related nodes", schema);

        return SyncToolSpecification.builder()
            .tool(tool)
            .callHandler((exchange, request) -> {
                try {
                    Map<String, Object> args = request.arguments();
                    String graphId = (String) args.get("graphId");
                    String nodeId = (String) args.get("nodeId");
                    int depth = args.get("depth") != null ? ((Number) args.get("depth")).intValue() : 1;

                    KgResult<Map<String, Object>> result = kgService.query(graphId, nodeId, depth);
                    return toCallToolResult(result);
                } catch (Exception e) {
                    return errorResult(e.getMessage());
                }
            })
            .build();
    }

    private SyncToolSpecification createKgList() {
        JsonSchema schema = buildSchema(
            Map.of(
                "graphId", Map.of("type", "string"),
                "type", Map.of("type", "string", "description", "Filter by node type")
            ),
            List.of("graphId")
        );
        Tool tool = buildTool("kg_list", "List nodes in the knowledge graph", schema);

        return SyncToolSpecification.builder()
            .tool(tool)
            .callHandler((exchange, request) -> {
                try {
                    Map<String, Object> args = request.arguments();
                    String graphId = (String) args.get("graphId");
                    String type = (String) args.get("type");

                    KgResult<List<KgNode>> result = kgService.list(graphId, type);
                    return toCallToolResult(result);
                } catch (Exception e) {
                    return errorResult(e.getMessage());
                }
            })
            .build();
    }

    private CallToolResult toCallToolResult(KgResult<?> result) {
        try {
            String json = objectMapper.writeValueAsString(result);
            return new CallToolResult(List.of(new TextContent(json)), false);
        } catch (Exception e) {
            return errorResult(e.getMessage());
        }
    }

    private CallToolResult errorResult(String message) {
        return new CallToolResult(List.of(new TextContent("{\"error\":\"" + message + "\"}")), true);
    }
}
