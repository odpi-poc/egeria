/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.assetcatalog.service;


import org.odpi.openmetadata.accessservices.assetcatalog.admin.AssetCatalogInstanceHandler;
import org.odpi.openmetadata.accessservices.assetcatalog.handlers.RelationshipHandler;
import org.odpi.openmetadata.accessservices.assetcatalog.model.Relationship;
import org.odpi.openmetadata.accessservices.assetcatalog.model.rest.responses.RelationshipResponse;
import org.odpi.openmetadata.commonservices.ffdc.RESTExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AssetCatalogRelationshipService provides the server-side implementation of the Asset Catalog Open Metadata
 * Assess Service (OMAS).
 * This service provide the functionality to fetch asset relationships and details about specific relationships.
 */
public class AssetCatalogRelationshipService {

    private static final Logger log = LoggerFactory.getLogger(AssetCatalogRelationshipService.class);
    private final AssetCatalogInstanceHandler instanceHandler = new AssetCatalogInstanceHandler();
    private final RESTExceptionHandler restExceptionHandler = new RESTExceptionHandler();

    public RelationshipResponse getRelationshipBetweenEntities(String serverName, String userId,
                                                               String entity1GUID,
                                                               String entity2GUID,
                                                               String relationshipTypeGUID) {
        final String methodName = "getRelationshipBetweenEntities";

        log.debug("Calling method: {}", methodName);

        RelationshipResponse response = new RelationshipResponse();

        try {
            RelationshipHandler relationshipHandler = instanceHandler.getRelationshipHandler(userId, serverName, methodName);
            Relationship relationshipBetweenEntities = relationshipHandler.getRelationshipBetweenEntities(userId, entity1GUID, entity2GUID, relationshipTypeGUID);
            response.setRelationship(relationshipBetweenEntities);
        } catch (org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException e) {
            restExceptionHandler.captureInvalidParameterException(response, e);
        } catch (org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException e) {
            restExceptionHandler.captureUserNotAuthorizedException(response, e);
        } catch (org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException e) {
            restExceptionHandler.capturePropertyServerException(response, e);
        }

        log.debug("Returning from method: {} with response: {}", methodName, response);

        return response;
    }
}
