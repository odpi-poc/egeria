/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.assetlineage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AssetContext {

    private Set<LineageEntity> vertices;
    private Set<Edge> edges;
    private Map<String, Set<Edge>> neighbors;

    public AssetContext() {
        vertices = new HashSet<>();
        edges = new HashSet<>();
        neighbors = new HashMap<>();
    }

    public boolean addVertex(LineageEntity Vertex) {
        return vertices.add(Vertex);
    }


    public boolean addEdge(Edge edge) {
       if (!edges.add(edge)) return false;

        String guid = edge.getFromVertex().getGuid();

        neighbors.putIfAbsent(guid, new HashSet<>());

        neighbors.get(guid).add(edge);

        return true;
    }


    public Map<String, Set<Edge>> getNeighbors() {
        return neighbors;
    }
}




