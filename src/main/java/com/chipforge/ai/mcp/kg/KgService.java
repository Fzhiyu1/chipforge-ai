package com.chipforge.ai.mcp.kg;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Knowledge graph service for CRUD operations.
 */
@Service
public class KgService {

    private static final String KG_DIR = ".chipforge/kg";
    private final ObjectMapper objectMapper;

    public KgService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // ========== Public API ==========

    public KgResult<Void> init(String graphId, List<KgNode> nodes, List<KgRelation> relations) {
        try {
            KgGraph graph = new KgGraph(graphId);
            graph.setNodes(nodes != null ? nodes : new ArrayList<>());
            graph.setRelations(relations != null ? relations : new ArrayList<>());
            saveGraph(graphId, graph);
            return KgResult.ok();
        } catch (IOException e) {
            return KgResult.failure(KgErrorCode.INVALID_ID);
        }
    }

    public KgResult<KgNode> createNode(String graphId, String id, String type, Map<String, Object> properties) {
        if (!KgNode.isValidId(id)) {
            return KgResult.failure(KgErrorCode.INVALID_ID);
        }
        if (!KgNode.isValidType(type)) {
            return KgResult.failure(KgErrorCode.INVALID_TYPE);
        }

        try {
            KgGraph graph = loadOrCreateGraph(graphId);
            if (findNode(graph, id).isPresent()) {
                return KgResult.failure(KgErrorCode.NODE_EXISTS);
            }

            KgNode node = new KgNode(id, type, properties != null ? properties : new HashMap<>());
            graph.getNodes().add(node);
            saveGraph(graphId, graph);
            return KgResult.ok(node);
        } catch (IOException e) {
            return KgResult.failure(KgErrorCode.INVALID_ID);
        }
    }

    public KgResult<KgRelation> createRelation(String graphId, String fromId, String toId, String type) {
        if (!KgRelation.isValidType(type)) {
            return KgResult.failure(KgErrorCode.INVALID_TYPE);
        }

        try {
            KgGraph graph = loadOrCreateGraph(graphId);
            if (findNode(graph, fromId).isEmpty()) {
                return KgResult.failure(KgErrorCode.NODE_NOT_FOUND);
            }
            if (findNode(graph, toId).isEmpty()) {
                return KgResult.failure(KgErrorCode.NODE_NOT_FOUND);
            }
            if (findRelation(graph, fromId, toId, type).isPresent()) {
                return KgResult.failure(KgErrorCode.RELATION_EXISTS);
            }

            KgRelation relation = new KgRelation(fromId, toId, type);
            graph.getRelations().add(relation);
            saveGraph(graphId, graph);
            return KgResult.ok(relation);
        } catch (IOException e) {
            return KgResult.failure(KgErrorCode.INVALID_ID);
        }
    }

    public KgResult<Void> deleteNode(String graphId, String id) {
        try {
            KgGraph graph = loadOrCreateGraph(graphId);
            if (findNode(graph, id).isEmpty()) {
                return KgResult.failure(KgErrorCode.NODE_NOT_FOUND);
            }
            if (hasRelations(graph, id)) {
                return KgResult.failure(KgErrorCode.HAS_RELATIONS);
            }

            graph.getNodes().removeIf(n -> n.id().equals(id));
            saveGraph(graphId, graph);
            return KgResult.ok();
        } catch (IOException e) {
            return KgResult.failure(KgErrorCode.INVALID_ID);
        }
    }

    public KgResult<Void> deleteRelation(String graphId, String fromId, String toId, String type) {
        try {
            KgGraph graph = loadOrCreateGraph(graphId);
            if (findRelation(graph, fromId, toId, type).isEmpty()) {
                return KgResult.failure(KgErrorCode.RELATION_NOT_FOUND);
            }

            graph.getRelations().removeIf(r ->
                r.from().equals(fromId) && r.to().equals(toId) && r.type().equals(type));
            saveGraph(graphId, graph);
            return KgResult.ok();
        } catch (IOException e) {
            return KgResult.failure(KgErrorCode.INVALID_ID);
        }
    }

    public KgResult<KgNode> updateNode(String graphId, String id, Map<String, Object> properties) {
        try {
            KgGraph graph = loadOrCreateGraph(graphId);
            Optional<KgNode> existing = findNode(graph, id);
            if (existing.isEmpty()) {
                return KgResult.failure(KgErrorCode.NODE_NOT_FOUND);
            }

            KgNode oldNode = existing.get();
            KgNode newNode = new KgNode(id, oldNode.type(), properties != null ? properties : new HashMap<>());
            graph.getNodes().removeIf(n -> n.id().equals(id));
            graph.getNodes().add(newNode);
            saveGraph(graphId, graph);
            return KgResult.ok(newNode);
        } catch (IOException e) {
            return KgResult.failure(KgErrorCode.INVALID_ID);
        }
    }

    public KgResult<Map<String, Object>> query(String graphId, String nodeId, int depth) {
        try {
            KgGraph graph = loadOrCreateGraph(graphId);
            if (findNode(graph, nodeId).isEmpty()) {
                return KgResult.failure(KgErrorCode.NODE_NOT_FOUND);
            }

            List<KgNode> nodes = bfsQuery(graph, nodeId, depth);
            List<KgRelation> relations = getRelationsForNodes(graph, nodes);

            Map<String, Object> result = new HashMap<>();
            result.put("nodes", nodes);
            result.put("relations", relations);
            return KgResult.ok(result);
        } catch (IOException e) {
            return KgResult.failure(KgErrorCode.INVALID_ID);
        }
    }

    public KgResult<List<KgNode>> list(String graphId, String type) {
        try {
            KgGraph graph = loadOrCreateGraph(graphId);
            List<KgNode> nodes = graph.getNodes();

            if (type != null && !type.isEmpty()) {
                nodes = nodes.stream()
                    .filter(n -> n.type().equals(type))
                    .toList();
            }
            return KgResult.ok(nodes);
        } catch (IOException e) {
            return KgResult.failure(KgErrorCode.INVALID_ID);
        }
    }

    // ========== Private Helpers ==========

    private Path getGraphPath(String graphId) {
        // Support path-style graphId (e.g., "test-skill/projects/udp_simple")
        if (graphId.contains("/") || graphId.contains("\\")) {
            return Paths.get(System.getProperty("user.dir"), graphId + ".kg.json");
        }
        return Paths.get(System.getProperty("user.dir"), KG_DIR, graphId + ".json");
    }

    private KgGraph loadOrCreateGraph(String graphId) throws IOException {
        Path path = getGraphPath(graphId);
        if (Files.exists(path)) {
            String json = Files.readString(path);
            return objectMapper.readValue(json, KgGraph.class);
        }
        return new KgGraph(graphId);
    }

    private void saveGraph(String graphId, KgGraph graph) throws IOException {
        Path path = getGraphPath(graphId);
        Files.createDirectories(path.getParent());
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(graph);
        Files.writeString(path, json);
    }

    private Optional<KgNode> findNode(KgGraph graph, String id) {
        return graph.getNodes().stream()
            .filter(n -> n.id().equals(id))
            .findFirst();
    }

    private Optional<KgRelation> findRelation(KgGraph graph, String from, String to, String type) {
        return graph.getRelations().stream()
            .filter(r -> r.from().equals(from) && r.to().equals(to) && r.type().equals(type))
            .findFirst();
    }

    private boolean hasRelations(KgGraph graph, String nodeId) {
        return graph.getRelations().stream()
            .anyMatch(r -> r.from().equals(nodeId) || r.to().equals(nodeId));
    }

    private List<KgNode> bfsQuery(KgGraph graph, String startId, int depth) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        Map<String, Integer> depthMap = new HashMap<>();

        queue.add(startId);
        visited.add(startId);
        depthMap.put(startId, 0);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentDepth = depthMap.get(current);

            if (currentDepth >= depth) {
                continue;
            }

            for (KgRelation r : graph.getRelations()) {
                String neighbor = null;
                if (r.from().equals(current)) {
                    neighbor = r.to();
                } else if (r.to().equals(current)) {
                    neighbor = r.from();
                }

                if (neighbor != null && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    depthMap.put(neighbor, currentDepth + 1);
                }
            }
        }

        return graph.getNodes().stream()
            .filter(n -> visited.contains(n.id()))
            .toList();
    }

    private List<KgRelation> getRelationsForNodes(KgGraph graph, List<KgNode> nodes) {
        Set<String> nodeIds = new HashSet<>();
        for (KgNode n : nodes) {
            nodeIds.add(n.id());
        }

        return graph.getRelations().stream()
            .filter(r -> nodeIds.contains(r.from()) && nodeIds.contains(r.to()))
            .toList();
    }
}
