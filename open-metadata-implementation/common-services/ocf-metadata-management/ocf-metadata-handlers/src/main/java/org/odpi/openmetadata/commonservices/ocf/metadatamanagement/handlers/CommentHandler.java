/* SPDX-License-Identifier: Apache 2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.commonservices.ocf.metadatamanagement.handlers;

import org.odpi.openmetadata.commonservices.ffdc.InvalidParameterHandler;
import org.odpi.openmetadata.commonservices.ocf.metadatamanagement.builders.CommentBuilder;
import org.odpi.openmetadata.commonservices.ocf.metadatamanagement.converters.CommentConverter;
import org.odpi.openmetadata.commonservices.ocf.metadatamanagement.mappers.AssetMapper;
import org.odpi.openmetadata.commonservices.ocf.metadatamanagement.mappers.CommentMapper;
import org.odpi.openmetadata.commonservices.ocf.metadatamanagement.mappers.ReferenceableMapper;
import org.odpi.openmetadata.commonservices.repositoryhandler.RepositoryHandler;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.Comment;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.CommentType;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.EntityDetail;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.EntityProxy;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.Relationship;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * CommentHandler manages Comment objects.  It runs server-side in
 * OMAS and retrieves Comment entities through the OMRSRepositoryConnector.
 */
public class CommentHandler
{
    private String                  serviceName;
    private String                  serverName;
    private OMRSRepositoryHelper    repositoryHelper;
    private RepositoryHandler       repositoryHandler;
    private InvalidParameterHandler invalidParameterHandler;
    private LastAttachmentHandler   lastAttachmentHandler;
    private int                     maxPageSize;


    /**
     * Construct the handler information needed to interact with the repository services
     *
     * @param serviceName      name of this service
     * @param serverName       name of the local server
     * @param invalidParameterHandler handler for managing parameter errors
     * @param repositoryHandler     manages calls to the repository services
     * @param repositoryHelper provides utilities for manipulating the repository services objects
     * @param lastAttachmentHandler handler for recording last attachment
     * @param maxPageSize maximum page size
     */
    public CommentHandler(String                  serviceName,
                          String                  serverName,
                          InvalidParameterHandler invalidParameterHandler,
                          RepositoryHandler       repositoryHandler,
                          OMRSRepositoryHelper    repositoryHelper,
                          LastAttachmentHandler   lastAttachmentHandler,
                          int                     maxPageSize)
    {
        this.serviceName = serviceName;
        this.serverName = serverName;
        this.invalidParameterHandler = invalidParameterHandler;
        this.repositoryHandler = repositoryHandler;
        this.repositoryHelper = repositoryHelper;
        this.lastAttachmentHandler = lastAttachmentHandler;
        this.maxPageSize = maxPageSize;
    }


    /**
     * Work out whether the requesting user is able to see the attached feedback.
     *
     * @param userId calling user
     * @param relationship relationship to the feedback content
     * @param methodName calling method
     * @return boolean - true if allowed
     */
    private boolean  visibleToUser(String        userId,
                                   Relationship  relationship,
                                   String        methodName)
    {
        if (userId.equals(relationship.getCreatedBy()))
        {
            return true;
        }

        return repositoryHelper.getBooleanProperty(serviceName,
                                                   CommentMapper.IS_PUBLIC_PROPERTY_NAME,
                                                   relationship.getProperties(),
                                                   methodName);
    }


    /**
     * Count the number of comments attached to an anchor entity.
     *
     * @param userId     calling user
     * @param anchorGUID identifier for the entity that the object is attached to
     * @param methodName calling method
     * @return count of attached objects
     * @throws InvalidParameterException  the parameters are invalid
     * @throws UserNotAuthorizedException user not authorized to issue this request
     * @throws PropertyServerException    problem accessing the property server
     */
    int countAttachedComments(String   userId,
                              String   anchorGUID,
                              String   methodName) throws InvalidParameterException,
                                                          PropertyServerException,
                                                          UserNotAuthorizedException
    {
        final String guidParameterName      = "anchorGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(anchorGUID, guidParameterName, methodName);

        List<Relationship> relationships = repositoryHandler.getRelationshipsByType(userId,
                                                                                    anchorGUID,
                                                                                    ReferenceableMapper.REFERENCEABLE_TYPE_NAME,
                                                                                    CommentMapper.REFERENCEABLE_TO_COMMENT_TYPE_GUID,
                                                                                    CommentMapper.REFERENCEABLE_TO_COMMENT_TYPE_NAME,
                                                                                    methodName);

        int count = 0;

        if (relationships != null)
        {
            for (Relationship relationship : relationships)
            {
                if (relationship != null)
                {
                    if (this.visibleToUser(userId, relationship, methodName))
                    {
                        count ++;
                    }
                }
            }
        }

        return count;
    }


    /**
     * Return the comments attached to an anchor entity. (No special security checking is required).
     *
     * @param userId     calling user
     * @param anchorGUID identifier for the entity that the feedback is attached to
     * @param anchorGUIDTypeName name of the type of the anchor entity
     * @param startingFrom where to start from in the list
     * @param pageSize maximum number of results that can be returned
     * @param methodName calling method
     *
     * @return list of retrieved objects
     *
     * @throws InvalidParameterException  the input properties are invalid
     * @throws UserNotAuthorizedException user not authorized to issue this request
     * @throws PropertyServerException    problem accessing the property server
     */
    public List<Comment>  getComments(String   userId,
                                      String   anchorGUID,
                                      String   anchorGUIDTypeName,
                                      int      startingFrom,
                                      int      pageSize,
                                      String   methodName) throws InvalidParameterException,
                                                                  PropertyServerException,
                                                                  UserNotAuthorizedException
    {
        final String guidParameter = "anchorGUID";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(anchorGUID, guidParameter, methodName);

        List<Relationship>  attachedComments = repositoryHandler.getPagedRelationshipsByType(userId,
                                                                                            anchorGUID,
                                                                                            anchorGUIDTypeName,
                                                                                            CommentMapper.REFERENCEABLE_TO_COMMENT_TYPE_GUID,
                                                                                            CommentMapper.REFERENCEABLE_TO_COMMENT_TYPE_NAME,
                                                                                            startingFrom,
                                                                                            pageSize,
                                                                                            methodName);

        if ((attachedComments == null) || (attachedComments.isEmpty()))
        {
            return null;
        }

        List<Comment>  results = new ArrayList<>();

        for (Relationship  relationship : attachedComments)
        {
            if (relationship != null)
            {
                Comment bean = this.getComment(userId, relationship, methodName);

                if (bean != null)
                {
                    results.add(bean);
                }
            }
        }

        if (results.isEmpty())
        {
            return null;
        }
        else
        {
            return results;
        }
    }


    /**
     * Retrieve the requested rating object.
     *
     * @param userId       calling user
     * @param relationship relationship between referenceable and rating
     * @param methodName   calling method
     * @return new bean
     * @throws InvalidParameterException  the parameters are invalid
     * @throws UserNotAuthorizedException user not authorized to issue this request
     * @throws PropertyServerException    problem accessing the property server
     */
    private Comment getComment(String       userId,
                               Relationship relationship,
                               String       methodName) throws InvalidParameterException,
                                                               PropertyServerException,
                                                               UserNotAuthorizedException
    {
        final String guidParameterName = "referenceableRatingRelationship.end2.guid";


        if (relationship != null)
        {
            if (this.visibleToUser(userId, relationship, methodName))
            {
                EntityProxy entityProxy = relationship.getEntityTwoProxy();

                if (entityProxy != null)
                {
                    EntityDetail entity = repositoryHandler.getEntityByGUID(userId,
                                                                            entityProxy.getGUID(),
                                                                            guidParameterName,
                                                                            CommentMapper.COMMENT_TYPE_NAME,
                                                                            methodName);

                    CommentConverter converter = new CommentConverter(entity,
                                                                      relationship,
                                                                      repositoryHelper,
                                                                      serviceName);

                    return converter.getBean();
                }
            }
        }

        return null;
    }



    /**
     * Adds a comment and link it to the supplied anchor entity.
     *
     * @param userId        String - userId of user making request.
     * @param anchorGUID    String - unique id for a referenceable entity that the comment is to be attached to.
     * @param guidParameter name of parameter that supplied the entity'ss unique identifier.
     * @param commentType   type of comment enum.
     * @param commentText   String - the text of the comment.
     * @param isPublic      should this be visible to all or private to the caller
     * @param methodName    calling method
     *
     * @return guid of new comment.
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws PropertyServerException There is a problem adding the asset properties to
     *                                   the property server.
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    private String attachNewComment(String      userId,
                                    String      anchorGUID,
                                    String      guidParameter,
                                    CommentType commentType,
                                    String      commentText,
                                    boolean     isPublic,
                                    String      methodName) throws InvalidParameterException,
                                                                   PropertyServerException,
                                                                   UserNotAuthorizedException
    {
        final String typeParameter = "commentType";
        final String textParameter = "commentText";

        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(anchorGUID, guidParameter, methodName);
        invalidParameterHandler.validateEnum(commentType, typeParameter, methodName);
        invalidParameterHandler.validateText(commentText, textParameter, methodName);

        repositoryHandler.validateEntityGUID(userId, anchorGUID, AssetMapper.ASSET_TYPE_NAME, methodName, guidParameter);

        CommentBuilder builder = new CommentBuilder(commentType,
                                                    commentText,
                                                    isPublic,
                                                    anchorGUID,
                                                    repositoryHelper,
                                                    serviceName,
                                                    serverName);

        String  commentGUID = repositoryHandler.createEntity(userId,
                                                             CommentMapper.COMMENT_TYPE_GUID,
                                                             CommentMapper.COMMENT_TYPE_NAME,
                                                             builder.getEntityInstanceProperties(methodName),
                                                             methodName);

        if (commentGUID != null)
        {
            repositoryHandler.createRelationship(userId,
                                                 CommentMapper.REFERENCEABLE_TO_COMMENT_TYPE_GUID,
                                                 anchorGUID,
                                                 commentGUID,
                                                 builder.getRelationshipInstanceProperties(methodName),
                                                 methodName);


        }

        return commentGUID;
    }


    /**
     * Adds a comment to the asset.
     *
     * @param userId        userId of user making request.
     * @param anchorGUID    unique identifier for the referenceable.
     * @param anchorType    type name for the referenceable.
     * @param commentType   type of comment enum.
     * @param commentText   the text of the comment.
     * @param isPublic      indicates whether the feedback should be shared or only be visible to the originating user
     * @param methodName    calling method
     *
     * @return guid of new comment.
     *
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws PropertyServerException there is a problem adding the asset properties to the property server.
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public String addCommentToReferenceable(String      userId,
                                            String      anchorGUID,
                                            String      anchorType,
                                            CommentType commentType,
                                            String      commentText,
                                            boolean     isPublic,
                                            String      methodName) throws InvalidParameterException,
                                                                           PropertyServerException,
                                                                           UserNotAuthorizedException
    {
        final String guidParameter = "anchorGUID";
        final String commentDescription = "New comment from ";

        String commentGUID = this.attachNewComment(userId,
                                                   anchorGUID,
                                                   guidParameter,
                                                   commentType,
                                                   commentText,
                                                   isPublic,
                                                   methodName);

        lastAttachmentHandler.updateLastAttachment(anchorGUID,
                                                   anchorType,
                                                   commentGUID,
                                                   CommentMapper.COMMENT_TYPE_NAME,
                                                   userId,
                                                   commentDescription + userId,
                                                   methodName);

        return commentGUID;
    }



    /**
     * Adds a comment to another comment.
     *
     * @param userId        userId of user making request.
     * @param anchorGUID    unique identifier for the referenceable.
     * @param anchorType    type name for the referenceable.
     * @param attachmentGUID unique identifier for an existing comment.  Used to add a reply to a comment.
     * @param commentType   type of comment enum.
     * @param commentText   the text of the comment.
     * @param isPublic      indicates whether the feedback should be shared or only be visible to the originating user
     * @param methodName    calling method
     *
     * @return guid of new comment.
     *
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws PropertyServerException there is a problem adding the asset properties to the property server.
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public String addCommentReplyToReferenceable(String      userId,
                                                 String      anchorGUID,
                                                 String      anchorType,
                                                 String      attachmentGUID,
                                                 CommentType commentType,
                                                 String      commentText,
                                                 boolean     isPublic,
                                                 String      methodName) throws InvalidParameterException,
                                                                                PropertyServerException,
                                                                                UserNotAuthorizedException
    {
        final String guidParameter = "attachmentGUID";
        final String commentDescription = "New comment reply from ";

        String commentGUID = this.attachNewComment(userId,
                                                   attachmentGUID,
                                                   guidParameter,
                                                   commentType,
                                                   commentText,
                                                   isPublic,
                                                   methodName);

        lastAttachmentHandler.updateLastAttachment(anchorGUID,
                                                   anchorType,
                                                   commentGUID,
                                                   CommentMapper.COMMENT_TYPE_NAME,
                                                   userId,
                                                   commentDescription + userId,
                                                   methodName);

        return commentGUID;
    }


    /**
     * Update an existing comment.
     *
     * @param userId        userId of user making request.
     * @param anchorGUID    unique identifier for the anchor entity
     * @param anchorType    type name for anchor
     * @param commentGUID   unique identifier for the comment to change.
     * @param commentType   type of comment enum.
     * @param commentText   the text of the comment.
     * @param isPublic      indicates whether the feedback should be shared or only be visible to the originating user
     * @param methodName    calling method
     *
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws PropertyServerException there is a problem adding the asset properties to the property server.
     * @throws UserNotAuthorizedException the requesting user is not authorized to issue this request.
     */
    public void   updateComment(String      userId,
                                String      anchorGUID,
                                String      anchorType,
                                String      commentGUID,
                                CommentType commentType,
                                String      commentText,
                                boolean     isPublic,
                                String      methodName) throws InvalidParameterException,
                                                               PropertyServerException,
                                                               UserNotAuthorizedException
    {
        final String typeParameter = "commentType";
        final String textParameter = "commentText";
        final String guidParameter = "commentGUID";
        final String commentDescription = "Updated comment from ";


        invalidParameterHandler.validateUserId(userId, methodName);
        invalidParameterHandler.validateGUID(commentGUID, guidParameter, methodName);
        invalidParameterHandler.validateEnum(commentType, typeParameter, methodName);
        invalidParameterHandler.validateText(commentText, textParameter, methodName);

        repositoryHandler.validateEntityGUID(userId, commentGUID, CommentMapper.COMMENT_TYPE_NAME, methodName, guidParameter);

        CommentBuilder builder = new CommentBuilder(commentType,
                                                    commentText,
                                                    isPublic,
                                                    anchorGUID,
                                                    repositoryHelper,
                                                    serviceName,
                                                    serverName);

        repositoryHandler.updateEntity(userId,
                                       commentGUID,
                                       CommentMapper.COMMENT_TYPE_GUID,
                                       CommentMapper.COMMENT_TYPE_NAME,
                                       builder.getEntityInstanceProperties(methodName),
                                       methodName);

        lastAttachmentHandler.updateLastAttachment(anchorGUID,
                                                   anchorType,
                                                   commentGUID,
                                                   CommentMapper.COMMENT_TYPE_NAME,
                                                   userId,
                                                   commentDescription + userId,
                                                   methodName);
    }


    /**
     * Removes a comment added to the asset by this user.
     *
     * @param userId       userId of user making request.
     * @param commentGUID  unique identifier for the comment object.
     * @param methodName    calling method
     *
     * @throws InvalidParameterException one of the parameters is null or invalid.
     * @throws PropertyServerException there is a problem updating the asset properties in the property server.
     * @throws UserNotAuthorizedException the user does not have permission to perform this request.
     */
    public void removeCommentFromReferenceable(String     userId,
                                               String     anchorGUID,
                                               String     anchorType,
                                               String     commentGUID,
                                               String     methodName) throws InvalidParameterException,
                                                                             PropertyServerException,
                                                                             UserNotAuthorizedException
    {
        final String commentDescription = "Removed comment from ";

        repositoryHandler.removeEntity(userId,
                                       commentGUID,
                                       CommentMapper.COMMENT_TYPE_GUID,
                                       CommentMapper.COMMENT_TYPE_NAME,
                                       null,
                                       null,
                                       methodName);

        lastAttachmentHandler.updateLastAttachment(anchorGUID,
                                                   anchorType,
                                                   commentGUID,
                                                   CommentMapper.COMMENT_TYPE_NAME,
                                                   userId,
                                                   commentDescription + userId,
                                                   methodName);
    }
}
