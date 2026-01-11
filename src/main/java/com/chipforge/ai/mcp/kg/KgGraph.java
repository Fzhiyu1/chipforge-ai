package com.chipforge.ai.mcp.kg;

import java.util.ArrayList;
import java.util.List;

/**
 * Knowledge graph container.
 */
public class KgGraph {
    private String graphId;
    private List<KgNode> nodes;
    private List<KgRelation> relations;

    public KgGraph() {
        this.nodes = new ArrayList<>();
        this.relations = new ArrayList<>();
    }

    public KgGraph(String graphId) {
        this();
        this.graphId = graphId;
    }

    public String getGraphId() {
        return graphId;
    }

    public void setGraphId(String graphId) {
        this.graphId = graphId;
    }

    public List<KgNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<KgNode> nodes) {
        this.nodes = nodes;
    }

    public List<KgRelation> getRelations() {
        return relations;
    }

    public void setRelations(List<KgRelation> relations) {
        this.relations = relations;
    }
}
