package com.chipforge.ai.mcp.kg;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KgServiceTest {

    private KgService kgService;
    private ObjectMapper objectMapper;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        kgService = new KgService(objectMapper);
        System.setProperty("user.dir", tempDir.toString());
    }

    @Test
    void testInit() {
        var nodes = List.of(
            new KgNode("clk", "Signal", Map.of("description", "Clock signal")),
            new KgNode("rst", "Signal", Map.of("description", "Reset signal"))
        );
        var relations = List.of(
            new KgRelation("clk", "rst", "RELATED")
        );

        var result = kgService.init("test", nodes, relations);

        assertTrue(result.success());
    }

    @Test
    void testCreateNode() {
        kgService.init("test", List.of(), List.of());

        var result = kgService.createNode("test", "clk", "Signal",
            Map.of("description", "Clock signal"));

        assertTrue(result.success());
        assertNotNull(result.data());
        assertEquals("clk", result.data().id());
    }

    @Test
    void testCreateNodeDuplicate() {
        kgService.init("test", List.of(), List.of());
        kgService.createNode("test", "clk", "Signal", Map.of());

        var result = kgService.createNode("test", "clk", "Signal", Map.of());

        assertFalse(result.success());
        assertEquals("NODE_EXISTS", result.code());
    }

    @Test
    void testCreateRelation() {
        kgService.init("test", List.of(), List.of());
        kgService.createNode("test", "clk", "Signal", Map.of());
        kgService.createNode("test", "rst", "Signal", Map.of());

        var result = kgService.createRelation("test", "clk", "rst", "RELATED");

        assertTrue(result.success());
    }

    @Test
    void testDeleteNode() {
        kgService.init("test", List.of(), List.of());
        kgService.createNode("test", "clk", "Signal", Map.of());

        var result = kgService.deleteNode("test", "clk");

        assertTrue(result.success());
    }

    @Test
    void testDeleteNodeWithRelations() {
        kgService.init("test", List.of(), List.of());
        kgService.createNode("test", "clk", "Signal", Map.of());
        kgService.createNode("test", "rst", "Signal", Map.of());
        kgService.createRelation("test", "clk", "rst", "RELATED");

        var result = kgService.deleteNode("test", "clk");

        assertFalse(result.success());
        assertEquals("HAS_RELATIONS", result.code());
    }

    @Test
    void testQuery() {
        kgService.init("test", List.of(), List.of());
        kgService.createNode("test", "clk", "Signal", Map.of());
        kgService.createNode("test", "rst", "Signal", Map.of());
        kgService.createNode("test", "data", "Signal", Map.of());
        kgService.createRelation("test", "clk", "rst", "RELATED");
        kgService.createRelation("test", "rst", "data", "RELATED");

        var result = kgService.query("test", "clk", 2);

        assertTrue(result.success());
        var nodes = (List<?>) result.data().get("nodes");
        assertEquals(3, nodes.size());
    }

    @Test
    void testList() {
        kgService.init("test", List.of(), List.of());
        kgService.createNode("test", "clk", "Signal", Map.of());
        kgService.createNode("test", "state1", "StateTransition", Map.of());

        var result = kgService.list("test", "Signal");

        assertTrue(result.success());
        assertEquals(1, result.data().size());
    }
}
