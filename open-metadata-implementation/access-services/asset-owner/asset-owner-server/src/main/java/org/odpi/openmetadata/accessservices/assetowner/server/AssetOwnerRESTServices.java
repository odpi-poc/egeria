/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.accessservices.assetowner.server;


import org.odpi.openmetadata.accessservices.assetowner.handlers.GovernanceZoneHandler;
import org.odpi.openmetadata.accessservices.assetowner.rest.ZoneListResponse;
import org.odpi.openmetadata.accessservices.assetowner.rest.ZoneResponse;
import org.odpi.openmetadata.commonservices.ocf.metadatamanagement.handlers.AssetHandler;
import org.odpi.openmetadata.commonservices.ocf.metadatamanagement.handlers.SchemaTypeHandler;
import org.odpi.openmetadata.commonservices.ocf.metadatamanagement.rest.*;
import org.odpi.openmetadata.accessservices.assetowner.rest.ZoneRequestBody;
import org.odpi.openmetadata.commonservices.ffdc.RESTExceptionHandler;
import org.odpi.openmetadata.commonservices.ffdc.rest.*;
import org.odpi.openmetadata.commonservices.odf.metadatamanagement.handlers.AnnotationHandler;
import org.odpi.openmetadata.commonservices.odf.metadatamanagement.handlers.DiscoveryAnalysisReportHandler;
import org.odpi.openmetadata.commonservices.odf.metadatamanagement.rest.AnnotationListResponse;
import org.odpi.openmetadata.commonservices.odf.metadatamanagement.rest.DiscoveryAnalysisReportListResponse;
import org.odpi.openmetadata.commonservices.odf.metadatamanagement.rest.StatusRequestBody;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.*;
import org.odpi.openmetadata.frameworks.discovery.properties.AnnotationStatus;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * AssetOwner provides the generic client-side interface for the Asset Owner Open Metadata Access Service (OMAS).
 * There are other clients that provide specialized methods for specific types of Asset.
 *
 * This client is initialized with the URL and name of the server that is running the Asset Owner OMAS.
 * This server is responsible for locating and managing the asset owner's definitions exchanged with this client.
 */
public class AssetOwnerRESTServices
{
    private static AssetOwnerInstanceHandler   instanceHandler     = new AssetOwnerInstanceHandler();

    private static final Logger log = LoggerFactory.getLogger(AssetOwnerRESTServices.class);

    private RESTExceptionHandler restExceptionHandler = new RESTExceptionHandler();


    /**
     * Default constructor
     */
    public AssetOwnerRESTServices()
    {
    }


    /*
     * ==============================================
     * AssetOnboardingInterface
     * ==============================================
     */


    /**
     * Add a simple asset description to the catalog.
     *
     * @param serverName name of the server instance to connect to
     * @param userId calling user (assumed to be the owner)
     * @param typeName specific type of the asset - this must match a defined subtype
     * @param requestBody other properties for asset
     *
     * @return unique identifier (guid) of the asset or
     * InvalidParameterException full path or userId is null or
     * PropertyServerException problem accessing property server or
     * UserNotAuthorizedException security access problem
     */
    public GUIDResponse  addAssetToCatalog(String           serverName,
                                           String           userId,
                                           String           typeName,
                                           AssetRequestBody requestBody)
    {
        final String   methodName = "addAssetToCatalog";

        log.debug("Calling method: " + methodName);

        GUIDResponse response = new GUIDResponse();
        OMRSAuditLog auditLog = null;

        try
        {
            if (requestBody != null)
            {
                auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
                AssetHandler handler = instanceHandler.getAssetHandler(userId, serverName, methodName);

                handler.addAsset(userId,
                                 typeName,
                                 requestBody.getQualifiedName(),
                                 requestBody.getDisplayName(),
                                 requestBody.getDescription(),
                                 requestBody.getAdditionalProperties(),
                                 requestBody.getExtendedProperties(),
                                 methodName);
            }
            else
            {
                restExceptionHandler.handleNoRequestBody(userId, methodName, serverName);
            }
        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());

        return response;
    }


    /**
     * Links the supplied schema to the asset.  If the schema is not defined in the metadata repository, it
     * is created.
     *
     * @param serverName name of the server instance to connect to
     * @param userId calling user
     * @param assetGUID unique identifier of the asset that the schema is to be attached to
     * @param requestBody schema to attach - a new schema is always created because schema can not be shared
     *                   between assets.
     *
     * @return void or
     * InvalidParameterException full path or userId is null or
     * PropertyServerException problem accessing property server or
     * UserNotAuthorizedException security access problem
     */
    public GUIDResponse   addSchemaToAsset(String             serverName,
                                           String             userId,
                                           String             assetGUID,
                                           SchemaRequestBody  requestBody)
    {
        final String   methodName = "addSchemaToAsset";

        log.debug("Calling method: " + methodName);

        GUIDResponse response = new GUIDResponse();
        OMRSAuditLog auditLog = null;

        try
        {
            if (requestBody != null)
            {
                auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
                AssetHandler      handler = instanceHandler.getAssetHandler(userId, serverName, methodName);

                handler.saveAssociatedSchemaType(userId,
                                                 assetGUID,
                                                 requestBody.getSchemaType(),
                                                 requestBody.getSchemaAttributes(),
                                                 methodName);
            }
            else
            {
                restExceptionHandler.handleNoRequestBody(userId, methodName, serverName);
            }
        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());

        return response;
    }


    /**
     * Adds attributes to a complex schema type like a relational table or a structured document.
     * This method can be called repeatedly to add many attributes to a schema.
     *
     * @param serverName name of the server instance to connect to
     * @param userId calling user
     * @param schemaTypeGUID unique identifier if the schema to anchor these attributes to.
     * @param requestBody list of schema attribute objects.
     *
     * @return void or
     * InvalidParameterException full path or userId is null or
     * PropertyServerException problem accessing property server or
     * UserNotAuthorizedException security access problem
     */
    public VoidResponse   addSchemaAttributesToSchema(String                 serverName,
                                                      String                 userId,
                                                      String                 schemaTypeGUID,
                                                      List<SchemaAttribute>  requestBody)
    {
        final String   methodName = "addSchemaAttributesToSchema";

        log.debug("Calling method: " + methodName);

        VoidResponse response = new VoidResponse();
        OMRSAuditLog auditLog = null;

        try
        {
            if (requestBody != null)
            {
                auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
                SchemaTypeHandler      handler = instanceHandler.getSchemaTypeHandler(userId, serverName, methodName);

                handler.saveSchemaAttributes(userId,
                                             schemaTypeGUID,
                                             requestBody,
                                             methodName);
            }
            else
            {
                restExceptionHandler.handleNoRequestBody(userId, methodName, serverName);
            }
        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());

        return response;
    }



    /**
     * Adds a connection to an asset.  Assets can have multiple connections attached.
     *
     * @param serverName name of the server instance to connect to
     * @param userId calling user
     * @param assetGUID unique identifier of the asset to attach the connection to
     * @param requestBody request body including a summary and connection object.
     *                   If the connection is already stored (matching guid)
     *                   then the existing connection is used.
     *
     * @return void or
     * InvalidParameterException full path or userId is null or
     * PropertyServerException problem accessing property server or
     * UserNotAuthorizedException security access problem
     */
    public VoidResponse addConnectionToAsset(String                serverName,
                                             String                userId,
                                             String                assetGUID,
                                             ConnectionRequestBody requestBody)
    {
        final String   methodName = "addConnectionToAsset";

        VoidResponse response = new VoidResponse();
        OMRSAuditLog auditLog = null;

        try
        {
            if (requestBody != null)
            {
                String     assetSummary = requestBody.getShortDescription();
                Connection connection = requestBody.getConnection();

                auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
                AssetHandler handler = instanceHandler.getAssetHandler(userId, serverName, methodName);

                handler.saveAssociatedConnection(userId,
                                                 assetGUID,
                                                 assetSummary,
                                                 connection,
                                                 methodName);
            }
            else
            {
                restExceptionHandler.handleNoRequestBody(userId, methodName, serverName);
            }

        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());

        return response;
    }



    /*
     * ==============================================
     * AssetClassificationInterface
     * ==============================================
     */


    /**
     * Create a simple relationship between a glossary term and an element in an Asset description (typically
     * a field in the schema).
     *
     * @param serverName name of the server instance to connect to
     * @param userId calling user
     * @param assetGUID unique identifier of the asset that is being described
     * @param glossaryTermGUID unique identifier of the glossary term
     * @param assetElementGUID element to link it to - its type must inherit from Referenceable.
     * @param requestBody null request body to satisfy POST request.
     *
     * @return void or
     * InvalidParameterException full path or userId is null or
     * PropertyServerException problem accessing property server or
     * UserNotAuthorizedException security access problem
     */
    public VoidResponse  addSemanticAssignment(String          serverName,
                                               String          userId,
                                               String          assetGUID,
                                               String          glossaryTermGUID,
                                               String          assetElementGUID,
                                               NullRequestBody requestBody)
    {
        final String   methodName = "addSemanticAssignment";

        VoidResponse response = new VoidResponse();
        OMRSAuditLog auditLog = null;

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
            AssetHandler   handler = instanceHandler.getAssetHandler(userId, serverName, methodName);

            handler.saveSemanticAssignment(userId,
                                           assetGUID,
                                           glossaryTermGUID,
                                           assetElementGUID,
                                           methodName);

        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());

        return response;
    }


    /**
     * Set up the labels that classify an asset's origin.
     *
     * @param serverName name of the server instance to connect to
     * @param userId calling user
     * @param assetGUID unique identifier of asset
     * @param requestBody Descriptive labels describing origin of the asset
     *
     * @return void or
     * InvalidParameterException full path or userId is null or
     * PropertyServerException problem accessing property server or
     * UserNotAuthorizedException security access problem
     */
    public VoidResponse  addAssetOrigin(String            serverName,
                                        String            userId,
                                        String            assetGUID,
                                        OriginRequestBody requestBody)
    {
        final String   methodName = "addAssetOrigin";

        log.debug("Calling method: " + methodName);

        VoidResponse response = new VoidResponse();
        OMRSAuditLog auditLog = null;

        try
        {
            if (requestBody != null)
            {
                auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
                AssetHandler      handler = instanceHandler.getAssetHandler(userId, serverName, methodName);

                handler.addAssetOrigin(userId,
                                       assetGUID,
                                       requestBody.getOrganizationGUID(),
                                       requestBody.getBusinessCapabilityGUID(),
                                       requestBody.getOtherOriginValues(),
                                       methodName);
            }
            else
            {
                restExceptionHandler.handleNoRequestBody(userId, methodName, serverName);
            }
        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());

        return response;
    }


    /*
     * ==============================================
     * AssetVisibilityInterface
     * ==============================================
     */


    /**
     * Create a definition of a governance zone.  The qualified name of these governance zones can be added
     * to the supportedZones and defaultZones properties of an OMAS to control which assets are processed
     * and how they are set up.  In addition the qualified names of zones can be added to Asset definitions
     * to indicate which zone(s) they belong to.
     *
     * @param serverName name of the server instance to connect to
     * @param userId calling user
     * @param requestBody other properties for a governance zone
     *
     * @return void or
     * InvalidParameterException full path or userId is null or
     * PropertyServerException problem accessing property server or
     * UserNotAuthorizedException security access problem
     */
    public VoidResponse  createGovernanceZone(String          serverName,
                                              String          userId,
                                              ZoneRequestBody requestBody)
    {
        final String   methodName = "createGovernanceZone";

        VoidResponse response = new VoidResponse();
        OMRSAuditLog auditLog = null;

        try
        {
            if (requestBody != null)
            {
                auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
                GovernanceZoneHandler handler = instanceHandler.getGovernanceZoneHandler(userId, serverName, methodName);

                handler.createGovernanceZone(userId,
                                             requestBody.getQualifiedName(),
                                             requestBody.getDisplayName(),
                                             requestBody.getDescription(),
                                             requestBody.getCriteria(),
                                             requestBody.getAdditionalProperties(),
                                             requestBody.getExtendedProperties(),
                                             methodName);
            }
            else
            {
                restExceptionHandler.handleNoRequestBody(userId, methodName, serverName);
            }
        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());
        return response;
    }


    /**
     * Return information about all of the governance zones.
     *
     * @param serverName name of the server instance to connect to
     * @param userId calling user
     * @param startingFrom position in the list (used when there are so many reports that paging is needed
     * @param maximumResults maximum number of elements to return an this call
     *
     * @return properties of the governance zone or
     * InvalidParameterException full path or userId is null or
     * PropertyServerException problem accessing property server or
     * UserNotAuthorizedException security access problem
     */
    public ZoneListResponse getGovernanceZones(String   serverName,
                                               String   userId,
                                               int      startingFrom,
                                               int      maximumResults)
    {
        final String   methodName = "getGovernanceZones";

        ZoneListResponse response = new ZoneListResponse();
        OMRSAuditLog auditLog = null;

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
            GovernanceZoneHandler handler = instanceHandler.getGovernanceZoneHandler(userId, serverName, methodName);

            response.setGovernanceZone(handler.getGovernanceZones(userId,
                                                                  startingFrom,
                                                                  maximumResults,
                                                                  methodName));
        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());
        return response;
    }


    /**
     * Return information about a specific governance zone.
     *
     * @param serverName name of the server instance to connect to
     * @param userId calling user
     * @param qualifiedName unique name for the zone
     *
     * @return properties of the governance zone or
     * InvalidParameterException full path or userId is null or
     * PropertyServerException problem accessing property server or
     * UserNotAuthorizedException security access problem
     */
    public ZoneResponse getGovernanceZone(String   serverName,
                                          String   userId,
                                          String   qualifiedName)
    {
        final String   methodName = "getGovernanceZone";

        ZoneResponse response = new ZoneResponse();
        OMRSAuditLog auditLog = null;

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
            GovernanceZoneHandler handler = instanceHandler.getGovernanceZoneHandler(userId, serverName, methodName);

            response.setGovernanceZone(handler.getGovernanceZone(userId,
                                                                 qualifiedName,
                                                                 methodName));
        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());
        return response;
    }



    /**
     * Update the zones for a specific asset.
     *
     * @param serverName name of the server instance to connect to
     * @param userId calling user
     * @param assetGUID unique identifier for the asset to update
     * @param assetZones list of zones for the asset - these values override the current values - null means belongs
     *                   to no zones.
     *
     * @return void or
     * InvalidParameterException full path or userId is null or
     * PropertyServerException problem accessing property server or
     * UserNotAuthorizedException security access problem
     */
    public VoidResponse updateAssetZones(String        serverName,
                                         String        userId,
                                         String        assetGUID,
                                         List<String>  assetZones)
    {
        final String   methodName = "updateAssetZones";

        log.debug("Calling method: " + methodName);

        VoidResponse response = new VoidResponse();
        OMRSAuditLog auditLog = null;

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
            AssetHandler      handler = instanceHandler.getAssetHandler(userId, serverName, methodName);

            handler.updateAssetZones(userId,
                                     assetGUID,
                                     assetZones,
                                     methodName);
        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());

        return response;
    }



    /**
     * Update the owner information for a specific asset.
     *
     * @param serverName name of the server instance to connect to
     * @param userId calling user
     * @param assetGUID unique identifier for the asset to update
     * @param requestBody values describing the new owner
     *
     * @return void or
     * InvalidParameterException full path or userId is null or
     * PropertyServerException problem accessing property server or
     * UserNotAuthorizedException security access problem
     */
    public VoidResponse  updateAssetOwner(String           serverName,
                                          String           userId,
                                          String           assetGUID,
                                          OwnerRequestBody requestBody)
    {
        final String   methodName = "updateAssetOwner";

        log.debug("Calling method: " + methodName);

        VoidResponse response = new VoidResponse();
        OMRSAuditLog auditLog = null;

        try
        {
            if (requestBody != null)
            {
                auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
                AssetHandler      handler = instanceHandler.getAssetHandler(userId, serverName, methodName);

                handler.updateAssetOwner(userId,
                                         assetGUID,
                                         requestBody.getOwnerId(),
                                         requestBody.getOwnerType(),
                                         methodName);
            }
            else
            {
                restExceptionHandler.handleNoRequestBody(userId, methodName, serverName);
            }
        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());

        return response;
    }


    /*
     * ==============================================
     * AssetReviewInterface
     * ==============================================
     */


    /**
     * Return a list of assets with the requested name.
     *
     * @param serverName name of the server instances for this request
     * @param userId calling user
     * @param name name to search for
     * @param startFrom starting element (used in paging through large result sets)
     * @param pageSize maximum number of results to return
     *
     * @return list of Asset summaries or
     * InvalidParameterException the name is invalid or
     * PropertyServerException there is a problem access in the property server or
     * UserNotAuthorizedException the user does not have access to the properties
     */
    public AssetsResponse getAssetsByName(String   serverName,
                                          String   userId,
                                          String   name,
                                          int      startFrom,
                                          int      pageSize)
    {
        final String methodName    = "getAssetsByName";

        log.debug("Calling method: " + methodName);

        AssetsResponse response = new AssetsResponse();
        OMRSAuditLog   auditLog = null;

        try
        {
            AssetHandler handler = instanceHandler.getAssetHandler(userId, serverName, methodName);

            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
            response.setAssets(handler.getAssetsByName(userId, name, startFrom, pageSize, methodName));
        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());

        return response;
    }


    /**
     * Return a list of assets with the requested search string in their name, qualified name
     * or description.
     *
     * @param serverName name of the server instances for this request
     * @param userId calling user
     * @param searchString string to search for in text
     * @param startFrom starting element (used in paging through large result sets)
     * @param pageSize maximum number of results to return
     *
     * @return list of assets that match the search string or
     * InvalidParameterException the searchString is invalid or
     * PropertyServerException there is a problem access in the property server or
     * UserNotAuthorizedException the user does not have access to the properties
     */
    public AssetsResponse  findAssets(String   serverName,
                                      String   userId,
                                      String   searchString,
                                      int      startFrom,
                                      int      pageSize)
    {
        final String methodName    = "findAssets";

        log.debug("Calling method: " + methodName);

        AssetsResponse response = new AssetsResponse();
        OMRSAuditLog   auditLog = null;

        try
        {
            AssetHandler handler = instanceHandler.getAssetHandler(userId, serverName, methodName);

            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
            response.setAssets(handler.findAssets(userId, searchString, startFrom, pageSize, methodName));
        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());

        return response;
    }


    /**
     * Return the discovery analysis reports about the asset.
     *
     * @param serverName name of the server instance to connect to
     * @param userId calling user
     * @param assetGUID unique identifier of the asset
     * @param startingFrom position in the list (used when there are so many reports that paging is needed
     * @param maxPageSize maximum number of elements to return an this call
     *
     * @return list of discovery analysis reports or
     * InvalidParameterException full path or userId is null or
     * PropertyServerException problem accessing property server or
     * UserNotAuthorizedException security access problem
     */
    public DiscoveryAnalysisReportListResponse getDiscoveryAnalysisReports(String  serverName,
                                                                           String  userId,
                                                                           String  assetGUID,
                                                                           int     startingFrom,
                                                                           int     maxPageSize)
    {
        final String   methodName = "getDiscoveryAnalysisReports";

        log.debug("Calling method: " + methodName);

        DiscoveryAnalysisReportListResponse response = new DiscoveryAnalysisReportListResponse();
        OMRSAuditLog auditLog = null;

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
            DiscoveryAnalysisReportHandler handler = instanceHandler.getDiscoveryAnalysisReportHandler(userId, serverName, methodName);

            response.setDiscoveryAnalysisReports(handler.getDiscoveryAnalysisReports(userId,
                                                                                     assetGUID,
                                                                                     startingFrom,
                                                                                     maxPageSize,
                                                                                     methodName));
        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());

        return response;
    }


    /**
     * Return the annotations linked directly to the report.
     *
     * @param serverName name of the server instance to connect to
     * @param userId identifier of calling user
     * @param discoveryReportGUID identifier of the discovery request.
     * @param startingFrom initial position in the stored list.
     * @param maximumResults maximum number of definitions to return on this call.
     * @param requestBody status of the desired annotations - null means all statuses.
     *
     * @return list of annotations or
     * InvalidParameterException full path or userId is null or
     * PropertyServerException problem accessing property server or
     * UserNotAuthorizedException security access problem
     */
    public AnnotationListResponse getDiscoveryReportAnnotations(String            serverName,
                                                                String            userId,
                                                                String            discoveryReportGUID,
                                                                int               startingFrom,
                                                                int               maximumResults,
                                                                StatusRequestBody requestBody)
    {
        final String   methodName = "getDiscoveryReportAnnotations";

        log.debug("Calling method: " + methodName);

        AnnotationListResponse response = new AnnotationListResponse();
        OMRSAuditLog auditLog = null;

        AnnotationStatus  annotationStatus = null;
        if (requestBody != null)
        {
            annotationStatus = requestBody.getAnnotationStatus();
        }

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
            DiscoveryAnalysisReportHandler handler = instanceHandler.getDiscoveryAnalysisReportHandler(userId, serverName, methodName);

            response.setAnnotations(handler.getDiscoveryReportAnnotations(userId,
                                                                          discoveryReportGUID,
                                                                          annotationStatus,
                                                                          startingFrom,
                                                                          maximumResults,
                                                                          methodName));
        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());

        return response;
    }



    /**
     * Return any annotations attached to this annotation.
     *
     * @param serverName name of the server instance to connect to
     * @param userId identifier of calling user
     * @param annotationGUID anchor annotation
     * @param startingFrom starting position in the list
     * @param maximumResults maximum number of annotations that can be returned.
     * @param requestBody status of the desired annotations - null means all statuses.
     *
     * @return list of Annotation objects or
     * InvalidParameterException full path or userId is null or
     * PropertyServerException problem accessing property server or
     * UserNotAuthorizedException security access problem
     */
    public AnnotationListResponse  getExtendedAnnotations(String            serverName,
                                                          String            userId,
                                                          String            annotationGUID,
                                                          int               startingFrom,
                                                          int               maximumResults,
                                                          StatusRequestBody requestBody)
    {
        final String   methodName = "getExtendedAnnotations";

        log.debug("Calling method: " + methodName);

        AnnotationListResponse response = new AnnotationListResponse();
        OMRSAuditLog auditLog = null;

        AnnotationStatus  annotationStatus = null;
        if (requestBody != null)
        {
            annotationStatus = requestBody.getAnnotationStatus();
        }

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
            AnnotationHandler handler = instanceHandler.getAnnotationHandler(userId, serverName, methodName);

            response.setAnnotations(handler.getExtendedAnnotations(userId,
                                                                   annotationGUID,
                                                                   annotationStatus,
                                                                   startingFrom,
                                                                   maximumResults,
                                                                   methodName));
        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());

        return response;
    }


    /*
     * ==============================================
     * AssetDecommissioningInterface
     * ==============================================
     */


    /**
     * Deletes an asset and all of its associated elements such as schema, connections (unless they are linked to
     * another asset), discovery reports and associated feedback.
     *
     * Given the depth of the delete performed by this call, it should be used with care.
     *
     * @param serverName name of the server instance to connect to
     * @param userId calling user
     * @param assetGUID unique identifier of the attest to attach the connection to
     * @param requestBody dummy request body to satisfy POST protocol.
     *
     * @return void or
     *  InvalidParameterException full path or userId is null or
     *  PropertyServerException problem accessing property server or
     *  UserNotAuthorizedException security access problem
     */
    public VoidResponse deleteAsset(String          serverName,
                                    String          userId,
                                    String          assetGUID,
                                    NullRequestBody requestBody)
    {
        final String methodName = "deleteAsset";

        log.debug("Calling method: " + methodName);

        VoidResponse response = new VoidResponse();
        OMRSAuditLog auditLog = null;

        try
        {
            auditLog = instanceHandler.getAuditLog(userId, serverName, methodName);
            AssetHandler handler = instanceHandler.getAssetHandler(userId, serverName, methodName);

            handler.removeAsset(userId, assetGUID, methodName);
        }
        catch (InvalidParameterException error)
        {
            restExceptionHandler.captureInvalidParameterException(response, error);
        }
        catch (PropertyServerException error)
        {
            restExceptionHandler.capturePropertyServerException(response, error);
        }
        catch (UserNotAuthorizedException error)
        {
            restExceptionHandler.captureUserNotAuthorizedException(response, error);
        }
        catch (Throwable error)
        {
            restExceptionHandler.captureThrowable(response, error, methodName, auditLog);
        }

        log.debug("Returning from method: " + methodName + " with response: " + response.toString());

        return response;
    }
}
