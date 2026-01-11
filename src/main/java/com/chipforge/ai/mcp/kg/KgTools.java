package com.chipforge.ai.mcp.kg;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
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

    private SyncToolSpecification createKgInit() {
        String schema = """
            {
                "type": "object",
                "properties": {
                    "graphId": {"type": "string", "description": "Graph identifier"},
                    "nodes": {"type": "string", "description": "JSON array of nodes"},
                    "relations": {"type": "string", "description": "JSON array of relations"}
                },
                "required": ["graphId", "nodes", "relations"]
            }
            """;

        return new SyncToolSpecification(
            new Tool("kg_init", "Initialize knowledge graph with nodes and relations", schema),
            (exchange, arguments) -> {
                try {
                    String graphId = (String) arguments.get("graphId");
                    String nodesJson = (String) arguments.get("nodes");
                    String relationsJson = (String) arguments.get("relations");

                    List<KgNode> nodes = objectMapper.readValue(nodesJson,
                        new TypeReference<List<KgNode>>() {});
                    List<KgRelation> relations = objectMapper.readValue(relationsJson,
                        new TypeReference<List<KgRelation>>() {});

                    KgResult<Void> result = kgService.init(graphId, nodes, relations);
                    return toCallToolResult(result);
                } catch (Exception e) {
                    return errorResult(e.getMessage());
                }
            }
        );
    }

    private SyncToolSpecification createKgCreateNode() {
        String schema = """
            {
                "type": "object",
                "properties": {
                    "graphId": {"type": "string"},
                    "id": {"type": "string"},
                    "type": {"type": "string", "enum": ["Signal", "StateTransition", "SignalExample"]},
                    "properties": {"type": "string", "description": "JSON object of properties"}
                },
                "required": ["graphId", "id", "type"]
            }
            """;

        return new SyncToolSpecification(
            new Tool("kg_create_node", "Create a new node in the knowledge graph", schema),
            (exchange, arguments) -> {
                try {
                    String graphId = (String) arguments.get("graphId");
                    String id = (String) arguments.get("id");
                    String type = (String) arguments.get("type");
                    String propsJson = (String) arguments.get("properties");

                    Map<String, Object> props = null;
                    if (propsJson != null && !propsJson.isEmpty()) {
                        props = objectMapper.readValue(propsJson,
                            new TypeReference<Map<String, Object>>() {});
                    }

                    KgResult<KgNode> result = kgService.createNode(graphId, id, type, props);
                    return toCallToolResult(result);
                } catch (Exception e) {
                    return errorResult(e.getMessage());
                }
            }
        );
    }

    private SyncToolSpecification createKgCreateRelation() {
        String schema = """
            {
                "type": "object",
                "properties": {
                    "graphId": {"type": "string"},
                    "fromId": {"type": "string"},
                    "toId": {"type": "string"},
                    "type": {"type": "string", "enum": ["EXAMPLES", "STATETRANSITION", "RELATED"]}
                },
                "required": ["graphId", "fromId", "toId", "type"]
            }
            """;

        return new SyncToolSpecification(
            new Tool("kg_create_relation", "Create a relation between two nodes", schema),
            (exchange, arguments) -> {
                try {
                    String graphId = (String) arguments.get("graphId");
                    String fromId = (String) arguments.get("fromId");
                    String toId = (String) arguments.get("toId");
                    String type = (String) arguments.get("type");

                    KgResult<KgRelation> result = kgService.createRelation(graphId, fromId, toId, type);
                    return toCallToolResult(result);
                } catch (Exception e) {
                    return errorResult(e.getMessage());
                }
            }
        );
    }

    private SyncToolSpecification createKgDeleteNode() {
        String schema = """
            {
                "type": "object",
                "properties": {
                    "graphId": {"type": "string"},
                    "id": {"type": "string"}
                },
                "required": ["graphId", "id"]
            }
            """;

        return new SyncToolSpecification(
            new Tool("kg_delete_node", "Delete a node from the knowledge graph", schema),
            (exchange, arguments) -> {
                try {
                    String graphId = (String) arguments.get("graphId");
                    String id = (String) arguments.get("id");

                    KgResult<Void> result = kgService.deleteNode(graphId, id);
                    return toCallToolResult(result);
                } catch (Exception e) {
                    return errorResult(e.getMessage());
                }
            }
        );
    }

    private SyncToolSpecification createKgDeleteRelation() {
        String schema = """
            {
                "type": "object",
                "properties": {
                    "graphId": {"type": "string"},
                    "fromId": {"type": "string"},
                    "toId": {"type": "string"},
                    "type": {"type": "string"}
                },
                "required": ["graphId", "fromId", "toId", "type"]
            }
            """;

        return new SyncToolSpecification(
            new Tool("kg_delete_relation", "Delete a relation from the knowledge graph", schema),
            (exchange, arguments) -> {
                try {
                    String graphId = (String) arguments.get("graphId");
                    String fromId = (String) arguments.get("fromId");
                    String toId = (String) arguments.get("toId");
                    String type = (String) arguments.get("type");

                    KgResult<Void> result = kgService.deleteRelation(graphId, fromId, toId, type);
                    return toCallToolResult(result);
                } catch (Exception e) {
                    return errorResult(e.getMessage());
                }
            }
        );
    }

    private SyncToolSpecification createKgUpdateNode() {
        String schema = """
            {
                "type": "object",
                "properties": {
                    "graphId": {"type": "string"},
                    "id": {"type": "string"},
                    "properties": {"type": "string", "description": "JSON object of properties"}
                },
                "required": ["graphId", "id", "properties"]
            }
            """;

        return new SyncToolSpecification(
            new Tool("kg_update_node", "Update node properties", schema),
            (exchange, arguments) -> {
                try {
                    String graphId = (String) arguments.get("graphId");
                    String id = (String) arguments.get("id");
                    String propsJson = (String) arguments.get("properties");

                    Map<String, Object> props = objectMapper.readValue(propsJson,
                        new TypeReference<Map<String, Object>>() {});

                    KgResult<KgNode> result = kgService.updateNode(graphId, id, props);
                    return toCallToolResult(result);
                } catch (Exception e) {
                    return errorResult(e.getMessage());
                }
            }
        );
    }

    private SyncToolSpecification createKgQuery() {
        String schema = """
            {
                "type": "object",
                "properties": {
                    "graphId": {"type": "string"},
                    "nodeId": {"type": "string"},
                    "depth": {"type": "integer", "default": 1}
                },
                "required": ["graphId", "nodeId"]
            }
            """;

        return new SyncToolSpecification(
            new Tool("kg_query", "BFS query related nodes", schema),
            (exchange, arguments) -> {
                try {
                    String graphId = (String) arguments.get("graphId");
                    String nodeId = (String) arguments.get("nodeId");
                    int depth = arguments.get("depth") != null
                        ? ((Number) arguments.get("depth")).intValue()
                        : 1;

                    KgResult<Map<String, Object>> result = kgService.query(graphId, nodeId, depth);
                    return toCallToolResult(result);
                } catch (Exception e) {
                    return errorResult(e.getMessage());
                }
            }
        );
    }

    private SyncToolSpecification createKgList() {
        String schema = """
            {
                "type": "object",
                "properties": {
                    "graphId": {"type": "string"},
                    "type": {"type": "string", "description": "Filter by node type"}
                },
                "required": ["graphId"]
            }
            """;

        return new SyncToolSpecification(
            new Tool("kg_list", "List nodes in the knowledge graph", schema),
            (exchange, arguments) -> {
                try {
                    String graphId = (String) arguments.get("graphId");
                    String type = (String) arguments.get("type");

                    KgResult<List<KgNode>> result = kgService.list(graphId, type);
                    return toCallToolResult(result);
                } catch (Exception e) {
                    return errorResult(e.getMessage());
                }
            }
        );
    }

    private CallToolResult toCallToolResult(KgResult<?> result) {
        try {
            String json = objectMapper.writeValueAsString(result);
            return new CallToolResult(json, !result.success());
        } catch (Exception e) {
            return errorResult(e.getMessage());
        }
    }

    private CallToolResult errorResult(String message) {
        return new CallToolResult("{\"success\":false,\"error\":\"" + message + "\"}", true);
    }
}
