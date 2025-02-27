/* SPDX-License-Identifier: Apache 2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.governanceservers.openlineage;

import org.odpi.openmetadata.accessservices.assetlineage.model.event.ProcessLineageEvent;
import org.odpi.openmetadata.governanceservers.openlineage.model.Scope;
import org.odpi.openmetadata.governanceservers.openlineage.model.View;

public interface OpenLineageGraphStore {

    /**
     * Process the serialized  information view event
     *
     * @param processLineageEvent event
     */
    void addEntity(ProcessLineageEvent processLineageEvent);

    /**
     * Returns a lineage subgraph.
     *
     * @param graphName main, buffer, mock, history.
     * @param scope     source-and-destination, end-to-end, ultimate-source, ultimate-destination, glossary.
     * @param view      The view queried by the user: hostview, tableview, columnview.
     * @param guid      The guid of the node of which the lineage is queried from.
     * @return A subgraph containing all relevant paths, in graphSON format.
     */
    String lineage(String graphName, Scope scope, View view, String guid);


    /**
     * Write an entire graph to disc in the Egeria root folder, in the .GraphMl format.
     *
     * @param graphName MAIN, BUFFER, MOCK, HISTORY.
     */
    void dumpGraph(String graphName);


    /**
     * Return an entire graph, in GraphSon format.
     *
     * @param graphName MAIN, BUFFER, MOCK, HISTORY.
     * @return The queried graph, in graphSON format.
     */
    String exportGraph(String graphName);
}
