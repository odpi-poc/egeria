/* SPDX-License-Identifier: Apache 2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.assetowner.client;

import org.odpi.openmetadata.accessservices.assetowner.api.AssetOnboardingAvroFileInterface;
import org.odpi.openmetadata.accessservices.assetowner.rest.NewFileAssetRequestBody;
import org.odpi.openmetadata.commonservices.ffdc.InvalidParameterHandler;
import org.odpi.openmetadata.commonservices.ffdc.RESTExceptionHandler;
import org.odpi.openmetadata.commonservices.ffdc.rest.GUIDListResponse;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;

import java.util.List;


/**
 * AvroFileAssetOwner provides specialist methods for working with Avro files.
 * This includes accessing the file and attempting to read the schema to catalog
 * its internal structure.  The file is opened client-side so that the metadata server does not
 * need access to the file contents.
 *
 */
public class AvroFileAssetOwner implements AssetOnboardingAvroFileInterface
{
    private String               serverName;               /* Initialized in constructor */
    private String               serverPlatformRootURL;    /* Initialized in constructor */
    private AssetOwnerRESTClient restClient;               /* Initialized in constructor */

    private InvalidParameterHandler invalidParameterHandler = new InvalidParameterHandler();
    private RESTExceptionHandler    exceptionHandler        = new RESTExceptionHandler();


    /**
     * Create a new client with no authentication embedded in the HTTP request.
     *
     * @param serverName name of the server to connect to
     * @param serverPlatformRootURL the network address of the server running the OMAS REST servers
     * @throws InvalidParameterException there is a problem creating the client-side components to issue any
     * REST API calls.
     */
    public AvroFileAssetOwner(String serverName,
                              String serverPlatformRootURL) throws InvalidParameterException
    {
        final String methodName = "Constructor (no security)";

        invalidParameterHandler.validateOMAGServerPlatformURL(serverPlatformRootURL, serverName, methodName);

        this.serverName = serverName;
        this.serverPlatformRootURL = serverPlatformRootURL;
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
    public AvroFileAssetOwner(String     serverName,
                              String     serverPlatformRootURL,
                              String     userId,
                              String     password) throws InvalidParameterException
    {
        final String methodName = "Constructor (with security)";

        invalidParameterHandler.validateOMAGServerPlatformURL(serverPlatformRootURL, serverName, methodName);

        this.serverName = serverName;
        this.serverPlatformRootURL = serverPlatformRootURL;
        this.restClient = new AssetOwnerRESTClient(serverName, serverPlatformRootURL, userId, password);
    }


    /*
     * ==============================================
     * AssetOnboardingFileSystem
     * ==============================================
     */


    private void addAvroSchemaToCatalog(String    userId,
                                        String    assetGUID,
                                        String    fullPath) throws InvalidParameterException,
                                                                   UserNotAuthorizedException,
                                                                   PropertyServerException
    {
        // todo
    }


    /**
     * Ensure the schema associated with an Avro file is correct.
     *
     * @param userId calling user
     * @param assetGUID unique identifier for the Avro file's asset in the catalog
     *
     * @throws InvalidParameterException full path or assetId is null
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public void refreshAvroSchemaInCatalog(String    userId,
                                           String    assetGUID) throws InvalidParameterException,
                                                                       UserNotAuthorizedException,
                                                                       PropertyServerException
    {
        // todo
    }


    /**
     * Add a simple asset description linked to a connection object for a Avro file.
     *
     * @param userId calling user (assumed to be the owner)
     * @param displayName display name for the file in the catalog
     * @param description description of the file in the catalog
     * @param fullPath full path of the file - used to access the file through the connector
     *
     * @return list of GUIDs from the top level to the root of the pathname
     *
     * @throws InvalidParameterException full path or userId is null
     * @throws PropertyServerException problem accessing property server
     * @throws UserNotAuthorizedException security access problem
     */
    public List<String> addAvroFileToCatalog(String    userId,
                                             String    displayName,
                                             String    description,
                                             String    fullPath) throws InvalidParameterException,
                                                                        UserNotAuthorizedException,
                                                                        PropertyServerException
    {
        final String   methodName = "addAvroFileToCatalog";
        final String   pathParameter = "fullPath";
        final String   urlTemplate = "/servers/{0}/open-metadata/access-services/asset-owner/users/{1}/assets/data-files/avro";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateName(fullPath, pathParameter, methodName);

        NewFileAssetRequestBody requestBody = new NewFileAssetRequestBody();
        requestBody.setDisplayName(displayName);
        requestBody.setDescription(description);
        requestBody.setFullPath(fullPath);

        GUIDListResponse restResult = restClient.callGUIDListPostRESTCall(methodName,
                                                                          serverPlatformRootURL + urlTemplate,
                                                                          requestBody,
                                                                          serverName,
                                                                          userId);

        exceptionHandler.detectAndThrowInvalidParameterException(methodName, restResult);
        exceptionHandler.detectAndThrowUserNotAuthorizedException(methodName, restResult);
        exceptionHandler.detectAndThrowPropertyServerException(methodName, restResult);

        List<String> fileAssetGUIDs = restResult.getGUIDs();
        if ((fileAssetGUIDs != null) && (! fileAssetGUIDs.isEmpty()))
        {
            String fileAssetGUID = fileAssetGUIDs.get(fileAssetGUIDs.size() - 1);

            this.addAvroSchemaToCatalog(userId, fileAssetGUID, fullPath);
        }

        return fileAssetGUIDs;
    }
}
