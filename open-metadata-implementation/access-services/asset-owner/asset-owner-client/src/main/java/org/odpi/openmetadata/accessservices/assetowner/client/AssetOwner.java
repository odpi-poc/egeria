/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.accessservices.assetowner.client;


import org.odpi.openmetadata.accessservices.assetowner.api.*;
import org.odpi.openmetadata.accessservices.assetowner.properties.GovernanceZone;
import org.odpi.openmetadata.accessservices.assetowner.rest.ZoneListResponse;
import org.odpi.openmetadata.accessservices.assetowner.rest.ZoneRequestBody;
import org.odpi.openmetadata.accessservices.assetowner.rest.ZoneResponse;
import org.odpi.openmetadata.commonservices.ffdc.rest.GUIDResponse;
import org.odpi.openmetadata.commonservices.ffdc.rest.NullRequestBody;
import org.odpi.openmetadata.commonservices.ffdc.rest.VoidResponse;
import org.odpi.openmetadata.commonservices.ocf.metadatamanagement.client.ConnectedAssetClientBase;
import org.odpi.openmetadata.commonservices.ocf.metadatamanagement.rest.*;
import org.odpi.openmetadata.commonservices.odf.metadatamanagement.rest.AnnotationListResponse;
import org.odpi.openmetadata.commonservices.odf.metadatamanagement.rest.DiscoveryAnalysisReportListResponse;
import org.odpi.openmetadata.commonservices.odf.metadatamanagement.rest.StatusRequestBody;
import org.odpi.openmetadata.frameworks.connectors.Connector;
import org.odpi.openmetadata.frameworks.connectors.ffdc.*;
import org.odpi.openmetadata.frameworks.connectors.properties.AssetUniverse;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.*;
import org.odpi.openmetadata.frameworks.discovery.properties.Annotation;
import org.odpi.openmetadata.frameworks.discovery.properties.AnnotationStatus;
import org.odpi.openmetadata.frameworks.discovery.properties.DiscoveryAnalysisReport;

import java.util.List;
import java.util.Map;

/**
 * AssetOwner provides the generic client-side interface for the Asset Owner Open Metadata Access Service (OMAS).
 * There are other clients that provide specialized methods for specific types of Asset.
 *
 * This client is initialized with the URL and name of the server that is running the Asset Owner OMAS.
 * This server is responsible for locating and managing the asset owner's definitions exchanged with this client.
 */
public class AssetOwner extends ConnectedAssetClientBase implements AssetOnboardingInterface,
                                                                    AssetClassificationInterface,
                                                                    AssetVisibilityInterface,
                                                                    AssetReviewInterface,
                                                                    AssetDecommissioningInterface

{
    private AssetOwnerRESTClient restClient;               /* Initialized in constructor */

    private static final String  serviceURLName = "asset-owner";


    /**
     * Create a new client with no authentication embedded in the HTTP request.
     *
     * @param serverName name of the server to connect to
     * @param serverPlatformRootURL the network address of the server running the OMAS REST servers
     * @throws InvalidParameterException there is a problem creating the client-side components to issue any
     * REST API calls.
     */
    public AssetOwner(String serverName,
                      String serverPlatformRootURL) throws InvalidParameterException
    {
        super(serverName, serverPlatformRootURL);

        this.restClient = new AssetOwnerRESTClient(serverName, serverPlatformRootURL);
    }


    /**
     * Create a new client that passes userId and password in each HTTP request.  This is the
     * userId/password of the calling server.  The end user's userId is sent on each request.
     *
     * @param serverName name of the server to connect to
     * @param serverPlatformRootURL the network address of the server running the OMAS REST servers
     * @param userId caller's userId embedded in all HTTP requests
     * @param password caller's userId embedded in all HTTP requests
     * @throws InvalidParameterException there is a problem creating the client-side components to issue any
     * REST API calls.
     */
    public AssetOwner(String serverName,
                      String serverPlatformRootURL,
                      String userId,
                      String password) throws InvalidParameterException
    {
        super(serverName, serverPlatformRootURL);

        this.restClient = new AssetOwnerRESTClient(serverName, serverPlatformRootURL, userId, password);
    }


    /*
     * ==============================================
     * AssetOnboardingInterface
     * ==============================================
     */


    /**
     * Add a simple asset description to the catalog.
     *
     * @param userId calling user (assumed to be the owner)
     * @param typeName specific type of the asset - this must match a defined subtype
     * @param qualifiedName unique name for the asset in the catalog
     * @param displayName display name for the asset in the catalog
     * @param description description of the asset in the catalog
     *
     * @return unique identifier (guid) of the asset
     *
     * @throws InvalidParameterException full path or userId is null
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public String  addAssetToCatalog(String               userId,
                                     String               typeName,
                                     String               qualifiedName,
                                     String               displayName,
                                     String               description,
                                     Map<String, String>  additionalProperties,
                                     Map<String, Object>  extendedProperties) throws InvalidParameterException,
                                                                                     UserNotAuthorizedException,
                                                                                     PropertyServerException
    {
        final String   methodName = "addAssetToCatalog";
        final String   typeNameParameter = "typeName";
        final String   qualifiedNameParameter = "qualifiedName";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/assets/{2}";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateName(typeName, typeNameParameter, methodName);
        invalidParameterHandler.validateName(qualifiedName, qualifiedNameParameter, methodName);

        AssetRequestBody  requestBody = new AssetRequestBody();

        requestBody.setQualifiedName(qualifiedName);
        requestBody.setDisplayName(displayName);
        requestBody.setDescription(description);
        requestBody.setAdditionalProperties(additionalProperties);
        requestBody.setExtendedProperties(extendedProperties);

        GUIDResponse restResult = restClient.callGUIDPostRESTCall(methodName,
                                                                  serverPlatformRootURL + urlTemplate,
                                                                  requestBody,
                                                                  serverName,
                                                                  userId,
                                                                  typeName);

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);

        return restResult.getGUID();
    }


    /**
     * Links the supplied schema to the asset.  If the schema has the GUID set, it is assumed to refer to
     * an existing schema defined in the metadata repository.  If this schema is either not found, or
     * already attached to an asset, then an error occurs.  If the GUID is null then a new schemaType
     * is added to the metadata repository and attached to the asset.  If another schema is currently
     * attached to the asset, it is unlinked and deleted.
     *
     * @param userId calling user
     * @param assetGUID unique identifier of the asset that the schema is to be attached to
     * @param schemaType schema to attach
     * @param schemaAttributes list of schema attribute objects.
     *
     * @return unique identifier (guid) of the schema
     *
     * @throws InvalidParameterException full path or userId is null
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public String   addSchemaToAsset(String                userId,
                                     String                assetGUID,
                                     SchemaType            schemaType,
                                     List<SchemaAttribute> schemaAttributes) throws InvalidParameterException,
                                                                                    UserNotAuthorizedException,
                                                                                    PropertyServerException
    {
        final String   methodName = "addSchemaToAsset";

        final String   assetGUIDParameter = "assetGUID";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/assets/{2}/schema-type";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(assetGUID, assetGUIDParameter, methodName);

        SchemaRequestBody  requestBody = new SchemaRequestBody();

        requestBody.setSchemaType(schemaType);
        requestBody.setSchemaAttributes(schemaAttributes);

        GUIDResponse restResult = restClient.callGUIDPostRESTCall(methodName,
                                                                  serverPlatformRootURL + urlTemplate,
                                                                  requestBody,
                                                                  serverName,
                                                                  userId,
                                                                  assetGUID);

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);

        return restResult.getGUID();

    }


    /**
     * Adds attributes to a complex schema type like a relational table or a structured document.
     * This method can be called repeatedly to add many attributes to a schema.
     *
     * @param userId calling user
     * @param schemaTypeGUID unique identifier if the schema to anchor these attributes to.
     * @param schemaAttributes list of schema attribute objects.
     *
     * @throws InvalidParameterException full path or userId is null
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public void   addSchemaAttributesToSchema(String                 userId,
                                              String                 schemaTypeGUID,
                                              List<SchemaAttribute>  schemaAttributes) throws InvalidParameterException,
                                                                                              UserNotAuthorizedException,
                                                                                              PropertyServerException
    {
        final String   methodName = "addSchemaAttributesToSchema";

        final String   schemaTypeGUIDParameter = "schemaTypeGUID";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/schemas/{2}/schema-attributes";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(schemaTypeGUID, schemaTypeGUIDParameter, methodName);

        VoidResponse restResult = restClient.callVoidPostRESTCall(methodName,
                                                                  serverPlatformRootURL + urlTemplate,
                                                                  schemaAttributes,
                                                                  serverName,
                                                                  userId,
                                                                  schemaTypeGUID);

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);
    }



    /**
     * Adds a connection to an asset.  Assets can have multiple connections attached.
     *
     * @param userId calling user
     * @param assetGUID unique identifier of the attest to attach the connection to
     * @param assetSummary summary of the asset that is stored in the relationship between the asset and the connection.
     * @param connection connection object.  If the connection is already stored (matching guid)
     *                   then the existing connection is used.
     * @throws InvalidParameterException full path or userId is null
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public void addConnectionToAsset(String     userId,
                                     String     assetGUID,
                                     String     assetSummary,
                                     Connection connection) throws InvalidParameterException,
                                                                   UserNotAuthorizedException,
                                                                   PropertyServerException
    {
        final String   methodName = "addConnectionToAsset";

        final String   assetGUIDParameter = "assetGUID";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/assets/{2}/connection";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(assetGUID, assetGUIDParameter, methodName);

        ConnectionRequestBody requestBody = new ConnectionRequestBody();

        requestBody.setShortDescription(assetSummary);
        requestBody.setConnection(connection);

        VoidResponse restResult = restClient.callVoidPostRESTCall(methodName,
                                                                  serverPlatformRootURL + urlTemplate,
                                                                  requestBody,
                                                                  serverName,
                                                                  userId,
                                                                  assetGUID);

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);
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
     * @param userId calling user
     * @param assetGUID unique identifier of asset
     * @param glossaryTermGUID unique identifier of the glossary term
     * @param assetElementGUID element to link it to - its type must inherit from Referenceable.
     *
     * @throws InvalidParameterException one of the parameters is null or invalid
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public void  addSemanticAssignment(String    userId,
                                       String    assetGUID,
                                       String    glossaryTermGUID,
                                       String    assetElementGUID) throws InvalidParameterException,
                                                                          UserNotAuthorizedException,
                                                                          PropertyServerException
    {
        final String   methodName = "addSemanticAssignment";
        final String   assetGUIDParameter = "assetGUID";
        final String   glossaryTermParameter = "glossaryTermGUID";
        final String   assetElementParameter = "assetElementGUID";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/assets/{2}/meanings/{3}/elements/{4}";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(assetGUID, assetGUIDParameter, methodName);
        invalidParameterHandler.validateGUID(glossaryTermGUID, glossaryTermParameter, methodName);
        invalidParameterHandler.validateGUID(assetElementGUID, assetElementParameter, methodName);

        VoidResponse restResult = restClient.callVoidPostRESTCall(methodName,
                                                                  serverPlatformRootURL + urlTemplate,
                                                                  nullRequestBody,
                                                                  serverName,
                                                                  userId,
                                                                  assetGUID,
                                                                  glossaryTermGUID,
                                                                  assetElementGUID);

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);
    }


    /**
     * Add the asset origin classification to an asset.
     *
     * @param userId calling user
     * @param assetGUID unique identifier of asset
     * @param organizationGUID Unique identifier (GUID) of the organization where this asset originated from - or null
     * @param businessCapabilityGUID  Unique identifier (GUID) of the business capability where this asset originated from.
     * @param otherOriginValues Descriptive labels describing origin of the asset
     * @throws InvalidParameterException entity not known, null userId or guid
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public void  addAssetOrigin(String                userId,
                                String                assetGUID,
                                String                organizationGUID,
                                String                businessCapabilityGUID,
                                Map<String, String>   otherOriginValues) throws InvalidParameterException,
                                                                                UserNotAuthorizedException,
                                                                                PropertyServerException
    {
        final String   methodName = "addAssetOrigin";
        final String   assetGUIDParameter = "assetGUID";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/assets/{2}/origin";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(assetGUID, assetGUIDParameter, methodName);

        OriginRequestBody requestBody = new OriginRequestBody();
        requestBody.setBusinessCapabilityGUID(businessCapabilityGUID);
        requestBody.setOrganizationGUID(organizationGUID);

        VoidResponse restResult = restClient.callVoidPostRESTCall(methodName,
                                                                  serverPlatformRootURL + urlTemplate,
                                                                  requestBody,
                                                                  serverName,
                                                                  userId,
                                                                  assetGUID);

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);
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
     * @param userId calling user
     * @param qualifiedName unique name for the zone - used in other configuration
     * @param displayName short display name for the zone
     * @param description description of the governance zone
     * @param criteria the criteria for inclusion in a governance zone
     * @param additionalProperties additional properties for a governance zone
     *
     * @throws InvalidParameterException qualifiedName or userId is null
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public void  createGovernanceZone(String              userId,
                                      String              qualifiedName,
                                      String              displayName,
                                      String              description,
                                      String              criteria,
                                      Map<String, String> additionalProperties) throws InvalidParameterException,
                                                                                       UserNotAuthorizedException,
                                                                                       PropertyServerException
    {
        final String   methodName = "createGovernanceZone";

        final String   qualifiedNameParameter = "qualifiedName";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/governance-zones";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateName(qualifiedName, qualifiedNameParameter, methodName);

        ZoneRequestBody requestBody = new ZoneRequestBody();

        requestBody.setQualifiedName(qualifiedName);
        requestBody.setDisplayName(displayName);
        requestBody.setDescription(description);
        requestBody.setAdditionalProperties(additionalProperties);

        VoidResponse restResult = restClient.callVoidPostRESTCall(methodName,
                                                                  serverPlatformRootURL + urlTemplate,
                                                                  requestBody,
                                                                  serverName,
                                                                  userId);

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);
    }



    /**
     * Return information about a specific governance zone.
     *
     * @param userId calling user
     * @param qualifiedName unique name for the zone
     *
     * @return properties of the governance zone
     * @throws InvalidParameterException qualifiedName or userId is null
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public GovernanceZone getGovernanceZone(String   userId,
                                            String   qualifiedName) throws InvalidParameterException,
                                                                           UserNotAuthorizedException,
                                                                           PropertyServerException
    {
        final String   methodName = "getGovernanceZone";

        final String   qualifiedNameParameter = "qualifiedName";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/governance-zones/name/{2}";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateName(qualifiedName, qualifiedNameParameter, methodName);

        ZoneResponse restResult = restClient.callZoneGetRESTCall(methodName,
                                                                 serverPlatformRootURL + urlTemplate,
                                                                 serverName,
                                                                 userId,
                                                                 qualifiedName);

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);

        return restResult.getGovernanceZone();
    }


    /**
     * Return information about the defined governance zones.
     *
     * @param userId calling user
     * @param startingFrom position in the list (used when there are so many reports that paging is needed
     * @param maximumResults maximum number of elements to return an this call
     *
     * @return properties of the governance zone
     *
     * @throws InvalidParameterException qualifiedName or userId is null
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public List<GovernanceZone>  getGovernanceZones(String   userId,
                                                    int      startingFrom,
                                                    int      maximumResults) throws InvalidParameterException,
                                                                                    UserNotAuthorizedException,
                                                                                    PropertyServerException
    {
        final String   methodName = "getGovernanceZones";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/governance-zones?startingFrom={4}&maximumResults={5}";

        invalidParameterHandler.validateUserId(userId, methodName);

        ZoneListResponse restResult = restClient.callZoneListGetRESTCall(methodName,
                                                                         serverPlatformRootURL + urlTemplate,
                                                                         serverName,
                                                                         userId,
                                                                         Integer.toString(startingFrom),
                                                                         Integer.toString(maximumResults));

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);

        return restResult.getGovernanceZones();
    }


    /**
     * Update the zones for a specific asset.
     *
     * @param userId calling user
     * @param assetGUID unique identifier for the asset to update
     * @param assetZones list of zones for the asset - these values override the current values - null means belongs
     *                   to no zones.
     * @throws InvalidParameterException guid or userId is null
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public void updateAssetZones(String        userId,
                                 String        assetGUID,
                                 List<String>  assetZones) throws InvalidParameterException,
                                                                  UserNotAuthorizedException,
                                                                  PropertyServerException
    {
        final String   methodName = "updateAssetZones";

        final String   assetGUIDParameter = "assetGUID";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/assets/{2}/governance-zones";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(assetGUID, assetGUIDParameter, methodName);

        VoidResponse restResult = restClient.callVoidPostRESTCall(methodName,
                                                                  serverPlatformRootURL + urlTemplate,
                                                                  assetZones,
                                                                  serverName,
                                                                  userId,
                                                                  assetGUID);

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);
    }



    /**
     * Update the owner information for a specific asset.
     *
     * @param userId calling user
     * @param assetGUID unique identifier for the asset to update
     * @param ownerId userId or profileGUID of the owner - or null to clear the field
     * @param ownerType indicator of the type of Id provides above - or null to clear the field
     * @throws InvalidParameterException userId is null
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public void updateAssetOwner(String    userId,
                                 String    assetGUID,
                                 String    ownerId,
                                 OwnerType ownerType) throws InvalidParameterException,
                                                             UserNotAuthorizedException,
                                                             PropertyServerException
    {
        final String   methodName = "updateAssetOwner";

        final String   assetGUIDParameter = "assetGUID";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/assets/{2}/owner";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(assetGUID, assetGUIDParameter, methodName);

        OwnerRequestBody requestBody = new OwnerRequestBody();
        requestBody.setOwnerId(ownerId);
        requestBody.setOwnerType(ownerType);

        VoidResponse restResult = restClient.callVoidPostRESTCall(methodName,
                                                                  serverPlatformRootURL + urlTemplate,
                                                                  requestBody,
                                                                  serverName,
                                                                  userId,
                                                                  assetGUID);

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);
    }



    /*
     * ==============================================
     * AssetReviewInterface
     * ==============================================
     */


    /**
     * Return a list of assets with the requested name.
     *
     * @param userId calling user
     * @param name name to search for
     * @param startFrom starting element (used in paging through large result sets)
     * @param pageSize maximum number of results to return
     *
     * @return list of unique identifiers of assets with matching name.
     *
     * @throws InvalidParameterException the name is invalid
     * @throws PropertyServerException there is a problem access in the property server
     * @throws UserNotAuthorizedException the user does not have access to the properties
     */
    public List<Asset> getAssetsByName(String   userId,
                                       String   name,
                                       int      startFrom,
                                       int      pageSize) throws InvalidParameterException,
                                                                 PropertyServerException,
                                                                 UserNotAuthorizedException
    {
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/assets/by-name?startFrom={2}&pageSize={3}";
        final String   methodName = "getAssetsByName";
        final String   nameParameter = "name";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateName(name, nameParameter, methodName);
        AssetsResponse restResult = restClient.callAssetsPostRESTCall(methodName,
                                                                      serverPlatformRootURL + urlTemplate,
                                                                      name,
                                                                      serverName,
                                                                      userId,
                                                                      startFrom,
                                                                      pageSize);

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);

        return restResult.getAssets();
    }


    /**
     * Return a list of assets with the requested search string in their name, qualified name
     * or description.
     *
     * @param userId calling user
     * @param searchString string to search for in text
     * @param startFrom starting element (used in paging through large result sets)
     * @param pageSize maximum number of results to return
     *
     * @return list of assets that match the search string.
     *
     * @throws InvalidParameterException the searchString is invalid
     * @throws PropertyServerException there is a problem access in the property server
     * @throws UserNotAuthorizedException the user does not have access to the properties
     */
    public List<Asset>  findAssets(String   userId,
                                   String   searchString,
                                   int      startFrom,
                                   int      pageSize) throws InvalidParameterException,
                                                             PropertyServerException,
                                                             UserNotAuthorizedException
    {
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/assets/by-search-string?startFrom={2}&pageSize={3}";
        final String   methodName = "findAssets";
        final String   searchParameter = "searchString";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateSearchString(searchString, searchParameter, methodName);
        AssetsResponse restResult = restClient.callAssetsPostRESTCall(methodName,
                                                                      serverPlatformRootURL + urlTemplate,
                                                                      searchString,
                                                                      serverName,
                                                                      userId,
                                                                      startFrom,
                                                                      pageSize);

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);

        return restResult.getAssets();
    }


    /**
     * Return the basic attributes of an asset.
     *
     * @param userId calling user
     * @param assetGUID unique identifier of the asset
     * @return basic asset properties
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws UserNotAuthorizedException user not authorized to issue this request.
     * @throws PropertyServerException there was a problem that occurred within the property server.
     */
    public Asset getAssetSummary(String  userId,
                                 String  assetGUID) throws InvalidParameterException,
                                                           UserNotAuthorizedException,
                                                           PropertyServerException
    {
        final String methodName = "getAssetSummary";

        final String assetGUIDParameter = "assetGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(assetGUID, assetGUIDParameter, methodName);

        return super.getAssetSummary(restClient, serviceURLName, userId, assetGUID, methodName);
    }


    /**
     * Returns a comprehensive collection of properties about the requested asset.
     *
     * @param userId         userId of user making request.
     * @param assetGUID      unique identifier for asset.
     *
     * @return a comprehensive collection of properties about the asset.
     *
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws PropertyServerException there is a problem retrieving the asset properties from the property servers).
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public AssetUniverse getAssetProperties(String userId,
                                            String assetGUID) throws InvalidParameterException,
                                                                     PropertyServerException,
                                                                     UserNotAuthorizedException
    {
        return super.getAssetProperties(serviceURLName, userId, assetGUID);
    }


    /**
     * Return a connector for the asset to enable the calling user to access the content.
     *
     * @param userId calling user
     * @param assetGUID unique identifier of the asset
     * @return connector object
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws UserNotAuthorizedException user not authorized to issue this request.
     * @throws PropertyServerException there was a problem that occurred within the property server.
     */
    public Connector getConnectorToAsset(String  userId,
                                         String  assetGUID) throws InvalidParameterException,
                                                                   UserNotAuthorizedException,
                                                                   PropertyServerException
    {
        final String   methodName = "getConnectorToAsset";

        final String assetGUIDParameter = "assetGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(assetGUID, assetGUIDParameter, methodName);

        try
        {
            return super.getConnectorForConnection(restClient,
                                                   serviceURLName,
                                                   userId,
                                                   getConnectionForAsset(restClient, serviceURLName, userId, assetGUID),
                                                   methodName);
        }
        catch (ConnectionCheckedException error)
        {
            throw new InvalidParameterException(error, "connection to asset " + assetGUID);
        }
        catch (ConnectorCheckedException  error)
        {
            throw new PropertyServerException(error);
        }
    }



    /**
     * Return the discovery analysis reports about the asset.
     *
     * @param userId calling user
     * @param assetGUID unique identifier of the asset
     * @param startingFrom position in the list (used when there are so many reports that paging is needed
     * @param maximumResults maximum number of elements to return an this call
     * @return list of discovery analysis reports
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws UserNotAuthorizedException user not authorized to issue this request.
     * @throws PropertyServerException there was a problem that occurred within the property server.
     */
    public List<DiscoveryAnalysisReport>   getDiscoveryAnalysisReports(String  userId,
                                                                       String  assetGUID,
                                                                       int     startingFrom,
                                                                       int     maximumResults) throws InvalidParameterException,
                                                                                                      UserNotAuthorizedException,
                                                                                                      PropertyServerException
    {
        final String   methodName = "getDiscoveryAnalysisReports";

        final String   assetGUIDParameter = "assetGUID";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/assets/{2}/discovery-analysis-reports?startingFrom={4}&maximumResults={5}";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(assetGUID, assetGUIDParameter, methodName);
        invalidParameterHandler.validatePaging(startingFrom, maximumResults, methodName);

        DiscoveryAnalysisReportListResponse restResult = restClient.callDiscoveryAnalysisReportListGetRESTCall(methodName,
                                                                                                               serverPlatformRootURL + urlTemplate,
                                                                                                               serverName,
                                                                                                               userId,
                                                                                                               assetGUID,
                                                                                                               Integer.toString(startingFrom),
                                                                                                               Integer.toString(maximumResults));

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);

        return restResult.getDiscoveryAnalysisReports();
    }



    /**
     * Return the annotations linked directly to the report.
     *
     * @param userId identifier of calling user
     * @param discoveryReportGUID identifier of the discovery request.
     * @param annotationStatus status of the desired annotations - null means all statuses.
     * @param startingFrom initial position in the stored list.
     * @param maximumResults maximum number of definitions to return on this call.
     *
     * @return list of annotations
     *
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws UserNotAuthorizedException user not authorized to issue this request.
     * @throws PropertyServerException there was a problem that occurred within the property server.
     */
    public List<Annotation> getDiscoveryReportAnnotations(String           userId,
                                                          String           discoveryReportGUID,
                                                          AnnotationStatus annotationStatus,
                                                          int              startingFrom,
                                                          int              maximumResults) throws InvalidParameterException,
                                                                                                  UserNotAuthorizedException,
                                                                                                  PropertyServerException
    {
        final String   methodName = "getDiscoveryReportAnnotations";

        final String   discoveryReportGUIDParameter = "discoveryReportGUID";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/discovery-analysis-reports/{2}?startingFrom={3}&maxPageSize={4}";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(discoveryReportGUID, discoveryReportGUIDParameter, methodName);
        invalidParameterHandler.validatePaging(startingFrom, maximumResults, methodName);

        StatusRequestBody  requestBody = new StatusRequestBody();

        requestBody.setAnnotationStatus(annotationStatus);

        AnnotationListResponse restResult = restClient.callAnnotationListPostRESTCall(methodName,
                                                                                      serverPlatformRootURL + urlTemplate,
                                                                                      requestBody,
                                                                                      serverName,
                                                                                      userId,
                                                                                      discoveryReportGUID,
                                                                                      Integer.toString(startingFrom),
                                                                                      Integer.toString(maximumResults));

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);

        return restResult.getAnnotations();
    }



    /**
     * Return any annotations attached to this annotation.
     *
     * @param userId identifier of calling user
     * @param annotationGUID anchor annotation
     * @param annotationStatus status of the desired annotations - null means all statuses.
     * @param startingFrom starting position in the list
     * @param maximumResults maximum number of annotations that can be returned.
     *
     * @return list of Annotation objects
     *
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws UserNotAuthorizedException user not authorized to issue this request.
     * @throws PropertyServerException there was a problem that occurred within the property server.
     */
    public List<Annotation>  getExtendedAnnotations(String           userId,
                                                    String           annotationGUID,
                                                    AnnotationStatus annotationStatus,
                                                    int              startingFrom,
                                                    int              maximumResults) throws InvalidParameterException,
                                                                                            UserNotAuthorizedException,
                                                                                            PropertyServerException
    {
        final String   methodName = "getExtendedAnnotations";

        final String   annotationGUIDParameter = "annotationGUID";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/annotations/{2}?startingFrom={3}&maxPageSize={4}";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(annotationGUID, annotationGUIDParameter, methodName);
        invalidParameterHandler.validatePaging(startingFrom, maximumResults, methodName);

        StatusRequestBody  requestBody = new StatusRequestBody();

        requestBody.setAnnotationStatus(annotationStatus);

        AnnotationListResponse restResult = restClient.callAnnotationListPostRESTCall(methodName,
                                                                                      serverPlatformRootURL + urlTemplate,
                                                                                      requestBody,
                                                                                      serverName,
                                                                                      userId,
                                                                                      annotationGUID,
                                                                                      Integer.toString(startingFrom),
                                                                                      Integer.toString(maximumResults));

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);

        return restResult.getAnnotations();
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
     * @param userId calling user
     * @param assetGUID unique identifier of the attest to attach the connection to
     * @throws InvalidParameterException full path or userId is null
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public void deleteAsset(String        userId,
                            String        assetGUID) throws InvalidParameterException,
                                                            UserNotAuthorizedException,
                                                            PropertyServerException
    {
        final String   methodName = "deleteAsset";

        final String   assetGUIDParameter = "assetGUID";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/assets/{2}/delete";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(assetGUID, assetGUIDParameter, methodName);

        NullRequestBody requestBody = new NullRequestBody();

        VoidResponse restResult = restClient.callVoidPostRESTCall(methodName,
                                                                  serverPlatformRootURL + urlTemplate,
                                                                  requestBody,
                                                                  serverName,
                                                                  userId,
                                                                  assetGUID);

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);
    }
}
