/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.repositoryservices.archiveutilities;


import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.*;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.typedefs.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * OMRSArchiveHelper provides utility methods to help in the construction of open metadata archives.
 */
public class OMRSArchiveHelper
{
    private OMRSArchiveBuilder     archiveBuilder;
    private String                 archiveGUID;
    private String                 archiveName;
    private String                 originatorName;
    private Date                   creationDate;
    private long                   versionNumber;
    private String                 versionName;
    private InstanceProvenanceType instanceProvenanceType = InstanceProvenanceType.CONTENT_PACK;
    private String                 license = null;


    /**
     * Constructor receives information about the archive being built.
     * This constructor is suitable if only creating typeDefs.
     *
     * @param archiveBuilder archive builder with the archive contents in it.
     * @param archiveGUID unique identifier for the archive.
     * @param originatorName name of the person / process creating the archive.
     * @param creationDate date that the archive was first built.
     * @param versionNumber version number of the archive.
     * @param versionName version name for the archive.
     */
    public OMRSArchiveHelper(OMRSArchiveBuilder     archiveBuilder,
                             String                 archiveGUID,
                             String                 originatorName,
                             Date                   creationDate,
                             long                   versionNumber,
                             String                 versionName)
    {
        this.archiveBuilder = archiveBuilder;
        this.archiveGUID = archiveGUID;
        this.originatorName = originatorName;
        this.creationDate = creationDate;
        this.versionNumber = versionNumber;
        this.versionName = versionName;
    }


    /**
     * Constructor receives information about the archive being built.
     * This constructor is suitable for creating TypeDefs and instances.
     *
     * @param archiveBuilder archive builder with the archive contents in it.
     * @param archiveGUID unique identifier for the archive.
     * @param archiveName unique name for the archive.
     * @param originatorName name of the person / process creating the archive.
     * @param creationDate date that the archive was first built.
     * @param versionNumber version number of the archive.
     * @param versionName version name for the archive.
     * @param instanceProvenanceType type of archive.
     * @param license license for the archive contents.
     */
    public OMRSArchiveHelper(OMRSArchiveBuilder     archiveBuilder,
                             String                 archiveGUID,
                             String                 archiveName,
                             String                 originatorName,
                             Date                   creationDate,
                             long                   versionNumber,
                             String                 versionName,
                             InstanceProvenanceType instanceProvenanceType,
                             String                 license)
    {
        this.archiveBuilder = archiveBuilder;
        this.archiveGUID = archiveGUID;
        this.archiveName = archiveName;
        this.originatorName = originatorName;
        this.creationDate = creationDate;
        this.versionNumber = versionNumber;
        this.versionName = versionName;
        this.instanceProvenanceType = instanceProvenanceType;
        this.license = license;
    }


    /**
     * Set up an individual primitive definition
     *
     * @param primitiveDefCategory category of the primitive def defines the unique
     *                             information about this primitive type.
     * @return initialized PrimitiveDef object ready for the archive
     */
    public PrimitiveDef getPrimitiveDef(PrimitiveDefCategory primitiveDefCategory)
    {
        PrimitiveDef  primitiveDef = new PrimitiveDef(primitiveDefCategory);

        primitiveDef.setGUID(primitiveDefCategory.getGUID());
        primitiveDef.setName(primitiveDefCategory.getName());
        primitiveDef.setVersion(versionNumber);
        primitiveDef.setVersionName(versionName);

        return primitiveDef;
    }


    /**
     * Create a CollectionDef for an Array.  A new CollectionDef is required for each combination of primitive types
     * used to initialize the collection.  Each CollectionDef has its own unique identifier (guid) and
     * its name is a combination of the collection type and the primitives use to initialize it.
     *
     * @param guid unique identifier for the CollectionDef
     * @param description short default description of the enum type
     * @param descriptionGUID guid of the glossary term describing this collection type
     * @param arrayType type of the array.
     * @return Filled out CollectionDef
     */
    public CollectionDef getArrayCollectionDef(String                guid,
                                               String                description,
                                               String                descriptionGUID,
                                               PrimitiveDefCategory  arrayType)
    {
        CollectionDef   collectionDef = new CollectionDef(CollectionDefCategory.OM_COLLECTION_ARRAY);

        collectionDef.setGUID(guid);
        collectionDef.setName("array<" + arrayType.getName() + ">");
        collectionDef.setDescription(description);
        collectionDef.setDescriptionGUID(descriptionGUID);
        collectionDef.setVersion(versionNumber);
        collectionDef.setVersionName(versionName);

        List<PrimitiveDefCategory> argumentList = new ArrayList<>();
        argumentList.add(arrayType);
        collectionDef.setArgumentTypes(argumentList);

        return collectionDef;
    }


    /**
     * Create a CollectionDef for a map.  A new CollectionDef is required for each combination of primitive types
     * used to initialize the collection.  Each CollectionDef has its own unique identifier (guid) and
     * its name is a combination of the collection type and the primitives use to initialize it.
     *
     * @param guid unique identifier for the CollectionDef
     * @param description short default description of the enum type
     * @param descriptionGUID guid of the glossary term describing this collection type
     * @param propertyKeyType type of the key for the map.
     * @param propertyValueType  type of map value.
     * @return Filled out CollectionDef
     */
    public CollectionDef getMapCollectionDef(String                guid,
                                             String                description,
                                             String                descriptionGUID,
                                             PrimitiveDefCategory  propertyKeyType,
                                             PrimitiveDefCategory  propertyValueType)
    {
        CollectionDef   collectionDef = new CollectionDef(CollectionDefCategory.OM_COLLECTION_MAP);

        collectionDef.setGUID(guid);
        collectionDef.setName("map<" + propertyKeyType.getName() + "," + propertyValueType.getName() + ">");
        collectionDef.setDescription(description);
        collectionDef.setDescriptionGUID(descriptionGUID);
        collectionDef.setVersion(versionNumber);
        collectionDef.setVersionName(versionName);

        ArrayList<PrimitiveDefCategory> argumentList = new ArrayList<>();
        argumentList.add(propertyKeyType);
        argumentList.add(propertyValueType);
        collectionDef.setArgumentTypes(argumentList);

        return collectionDef;
    }


    /**
     * Create an EnumDef that has no valid values defined.  These are added by the caller.
     *
     * @param guid unique identifier for the CollectionDef
     * @param name unique name for the CollectionDef
     * @param description short default description of the enum type
     * @param descriptionGUID guid of the glossary term describing this enum type
     * @return basic EnumDef without valid values
     */
    public EnumDef getEmptyEnumDef(String                guid,
                                   String                name,
                                   String                description,
                                   String                descriptionGUID)
    {
        EnumDef  enumDef = new EnumDef();

        enumDef.setGUID(guid);
        enumDef.setName(name);
        enumDef.setDescription(description);
        enumDef.setDescriptionGUID(descriptionGUID);
        enumDef.setDefaultValue(null);
        enumDef.setVersion(versionNumber);
        enumDef.setVersionName(versionName);

        return enumDef;
    }


    /**
     * Create an EnumElementDef that carries one of the valid values for an Enum.
     *
     * @param ordinal code number
     * @param value name
     * @param description short description
     * @param descriptionGUID guid of the glossary term describing this enum element
     * @return Fully filled out EnumElementDef
     */
    public EnumElementDef  getEnumElementDef(int     ordinal,
                                             String  value,
                                             String  description,
                                             String  descriptionGUID)
    {
        EnumElementDef   enumElementDef = new EnumElementDef();

        enumElementDef.setOrdinal(ordinal);
        enumElementDef.setValue(value);
        enumElementDef.setDescription(description);
        enumElementDef.setDescriptionGUID(descriptionGUID);

        return enumElementDef;
    }


    /**
     * Sets up a default EntityDef.  Calling methods can override the default values.  This EntityDef
     * has no attribute defined.
     *
     * @param guid unique identifier for the entity
     * @param name name of the entity
     * @param superType Super type for this entity (null for top-level)
     * @param description short description of the entity
     * @param descriptionGUID guid of the glossary term describing this entity type
     * @return Initialized EntityDef
     */
    public EntityDef  getDefaultEntityDef(String                  guid,
                                          String                  name,
                                          TypeDefLink             superType,
                                          String                  description,
                                          String                  descriptionGUID)
    {
        EntityDef entityDef = new EntityDef();

        /*
         * Set up the parameters supplied by the caller.
         */
        entityDef.setGUID(guid);
        entityDef.setName(name);
        entityDef.setSuperType(superType);
        entityDef.setDescription(description);
        entityDef.setDescriptionGUID(descriptionGUID);

        /*
         * Set up the defaults
         */
        entityDef.setOrigin(archiveGUID);
        entityDef.setCreatedBy(originatorName);
        entityDef.setCreateTime(creationDate);
        entityDef.setVersion(versionNumber);
        entityDef.setVersionName(versionName);

        /*
         * Set default valid instance statuses
         */
        ArrayList<InstanceStatus> validInstanceStatusList = new ArrayList<>();
        validInstanceStatusList.add(InstanceStatus.ACTIVE);
        validInstanceStatusList.add(InstanceStatus.DELETED);
        entityDef.setValidInstanceStatusList(validInstanceStatusList);
        entityDef.setInitialStatus(InstanceStatus.ACTIVE);

        return entityDef;
    }


    /**
     * Return an attribute with the supplied name and description that is of type String.  It is set up to be optional,
     * indexable (useful for searches) but the value does not need to be unique.
     * These are the typical values used for most open metadata attribute.
     * They can be changed by the caller once the TypeDefAttribute is returned.
     *
     * @param attributeName name of the attribute
     * @param attributeDescription short description for the attribute
     * @param attributeDescriptionGUID guid of the glossary term that describes this attribute.
     * @return  TypeDefAttribute of type string
     */
    public TypeDefAttribute  getStringTypeDefAttribute(String      attributeName,
                                                       String      attributeDescription,
                                                       String      attributeDescriptionGUID)
    {
        TypeDefAttribute     attribute = new TypeDefAttribute();

        attribute.setAttributeName(attributeName);
        attribute.setAttributeDescription(attributeDescription);
        attribute.setAttributeDescriptionGUID(attributeDescriptionGUID);
        attribute.setAttributeType(this.archiveBuilder.getPrimitiveDef(PrimitiveDefCategory.OM_PRIMITIVE_TYPE_STRING.getName()));
        attribute.setAttributeCardinality(AttributeCardinality.AT_MOST_ONE);
        attribute.setValuesMinCount(0);
        attribute.setValuesMaxCount(1);
        attribute.setIndexable(true);
        attribute.setUnique(false);
        attribute.setDefaultValue(null);
        attribute.setExternalStandardMappings(null);

        return attribute;
    }


    /**
     * Return an attribute with the supplied name and description that is of type int.  It is set up to be optional,
     * indexable (useful for searches) but the value does not need to be unique.
     * These are the typical values used for most open metadata attribute.
     * They can be changed by the caller once the TypeDefAttribute is returned.
     *
     * @param attributeName name of the attribute
     * @param attributeDescription short description for the attribute
     * @param attributeDescriptionGUID guid of the glossary term that describes this attribute.
     * @return  TypeDefAttribute of type string
     */
    public TypeDefAttribute  getIntTypeDefAttribute(String      attributeName,
                                                    String      attributeDescription,
                                                    String      attributeDescriptionGUID)
    {
        TypeDefAttribute     attribute = new TypeDefAttribute();

        attribute.setAttributeName(attributeName);
        attribute.setAttributeDescription(attributeDescription);
        attribute.setAttributeDescriptionGUID(attributeDescriptionGUID);
        attribute.setAttributeType(this.archiveBuilder.getPrimitiveDef(PrimitiveDefCategory.OM_PRIMITIVE_TYPE_INT.getName()));
        attribute.setAttributeCardinality(AttributeCardinality.AT_MOST_ONE);
        attribute.setValuesMinCount(0);
        attribute.setValuesMaxCount(1);
        attribute.setIndexable(true);
        attribute.setUnique(false);
        attribute.setDefaultValue(null);
        attribute.setExternalStandardMappings(null);

        return attribute;
    }


    /**
     * Return an attribute with the supplied name and description that is of type boolean.  It is set up to be optional,
     * indexable (useful for searches) but the value does not need to be unique.
     * These are the typical values used for most open metadata attribute.
     * They can be changed by the caller once the TypeDefAttribute is returned.
     *
     * @param attributeName name of the attribute
     * @param attributeDescription short description for the attribute
     * @param attributeDescriptionGUID guid of the glossary term that describes this attribute.
     * @return  TypeDefAttribute of type string
     */
    public TypeDefAttribute  getBooleanTypeDefAttribute(String      attributeName,
                                                        String      attributeDescription,
                                                        String      attributeDescriptionGUID)
    {
        TypeDefAttribute     attribute = new TypeDefAttribute();

        attribute.setAttributeName(attributeName);
        attribute.setAttributeDescription(attributeDescription);
        attribute.setAttributeDescriptionGUID(attributeDescriptionGUID);
        attribute.setAttributeType(this.archiveBuilder.getPrimitiveDef(PrimitiveDefCategory.OM_PRIMITIVE_TYPE_BOOLEAN.getName()));
        attribute.setAttributeCardinality(AttributeCardinality.AT_MOST_ONE);
        attribute.setValuesMinCount(0);
        attribute.setValuesMaxCount(1);
        attribute.setIndexable(true);
        attribute.setUnique(false);
        attribute.setDefaultValue(null);
        attribute.setExternalStandardMappings(null);

        return attribute;
    }
    
    
    /**
     * Return an attribute with the supplied name and description that is of type date.  It is set up to be optional,
     * indexable (useful for searches) but the value does not need to be unique.
     * These are the typical values used for most open metadata attribute.
     * They can be changed by the caller once the TypeDefAttribute is returned.
     *
     * @param attributeName name of the attribute
     * @param attributeDescription short description for the attribute
     * @param attributeDescriptionGUID guid of the glossary term that describes this attribute.
     * @return  TypeDefAttribute of type string
     */
    public TypeDefAttribute  getDateTypeDefAttribute(String      attributeName,
                                                     String      attributeDescription,
                                                     String      attributeDescriptionGUID)
    {
        TypeDefAttribute     attribute = new TypeDefAttribute();

        attribute.setAttributeName(attributeName);
        attribute.setAttributeDescription(attributeDescription);
        attribute.setAttributeDescriptionGUID(attributeDescriptionGUID);
        attribute.setAttributeType(this.archiveBuilder.getPrimitiveDef(PrimitiveDefCategory.OM_PRIMITIVE_TYPE_DATE.getName()));
        attribute.setAttributeCardinality(AttributeCardinality.AT_MOST_ONE);
        attribute.setValuesMinCount(0);
        attribute.setValuesMaxCount(1);
        attribute.setIndexable(true);
        attribute.setUnique(false);
        attribute.setDefaultValue(null);
        attribute.setExternalStandardMappings(null);

        return attribute;
    }


    /**
     * Return an attribute with the supplied name and description that is of type long.  It is set up to be optional,
     * indexable (useful for searches) but the value does not need to be unique.
     * These are the typical values used for most open metadata attribute.
     * They can be changed by the caller once the TypeDefAttribute is returned.
     *
     * @param attributeName name of the attribute
     * @param attributeDescription short description for the attribute
     * @param attributeDescriptionGUID guid of the glossary term that describes this attribute.
     * @return  TypeDefAttribute of type string
     */
    public TypeDefAttribute  getLongTypeDefAttribute(String      attributeName,
                                                     String      attributeDescription,
                                                     String      attributeDescriptionGUID)
    {
        TypeDefAttribute     attribute = new TypeDefAttribute();

        attribute.setAttributeName(attributeName);
        attribute.setAttributeDescription(attributeDescription);
        attribute.setAttributeDescriptionGUID(attributeDescriptionGUID);
        attribute.setAttributeType(this.archiveBuilder.getPrimitiveDef(PrimitiveDefCategory.OM_PRIMITIVE_TYPE_LONG.getName()));
        attribute.setAttributeCardinality(AttributeCardinality.AT_MOST_ONE);
        attribute.setValuesMinCount(0);
        attribute.setValuesMaxCount(1);
        attribute.setIndexable(true);
        attribute.setUnique(false);
        attribute.setDefaultValue(null);
        attribute.setExternalStandardMappings(null);

        return attribute;
    }


    /**
     * Return an attribute with the supplied name and description that is of type long.  It is set up to be optional,
     * indexable (useful for searches) but the value does not need to be unique.
     * These are the typical values used for most open metadata attribute.
     * They can be changed by the caller once the TypeDefAttribute is returned.
     *
     * @param attributeName name of the attribute
     * @param attributeDescription short description for the attribute
     * @param attributeDescriptionGUID guid of the glossary term that describes this attribute.
     * @return  TypeDefAttribute of type string
     */
    public TypeDefAttribute  getFloatTypeDefAttribute(String      attributeName,
                                                      String      attributeDescription,
                                                      String      attributeDescriptionGUID)
    {
        TypeDefAttribute     attribute = new TypeDefAttribute();

        attribute.setAttributeName(attributeName);
        attribute.setAttributeDescription(attributeDescription);
        attribute.setAttributeDescriptionGUID(attributeDescriptionGUID);
        attribute.setAttributeType(this.archiveBuilder.getPrimitiveDef(PrimitiveDefCategory.OM_PRIMITIVE_TYPE_FLOAT.getName()));
        attribute.setAttributeCardinality(AttributeCardinality.AT_MOST_ONE);
        attribute.setValuesMinCount(0);
        attribute.setValuesMaxCount(1);
        attribute.setIndexable(true);
        attribute.setUnique(false);
        attribute.setDefaultValue(null);
        attribute.setExternalStandardMappings(null);

        return attribute;
    }


    /**
     * Return an attribute with the supplied name and description that is an array of strings.
     * It is set up to be optional,
     * indexable (useful for searches) but the value does not need to be unique.
     * These are the typical values used for most open metadata attribute.
     * They can be changed by the caller once the TypeDefAttribute is returned.
     *
     * @param attributeName name of the attribute
     * @param attributeDescription short description for the attribute
     * @param attributeDescriptionGUID guid of the glossary term that describes this attribute.
     * @return  TypeDefAttribute of type string
     */
    public TypeDefAttribute  getArrayStringTypeDefAttribute(String      attributeName,
                                                            String      attributeDescription,
                                                            String      attributeDescriptionGUID)
    {
        TypeDefAttribute     attribute = new TypeDefAttribute();

        attribute.setAttributeName(attributeName);
        attribute.setAttributeDescription(attributeDescription);
        attribute.setAttributeDescriptionGUID(attributeDescriptionGUID);
        attribute.setAttributeType(this.archiveBuilder.getCollectionDef("array<string>"));
        attribute.setAttributeCardinality(AttributeCardinality.AT_MOST_ONE);
        attribute.setValuesMinCount(0);
        attribute.setValuesMaxCount(1);
        attribute.setIndexable(true);
        attribute.setUnique(false);
        attribute.setDefaultValue(null);
        attribute.setExternalStandardMappings(null);

        return attribute;
    }


    /**
     * Return an attribute with the supplied name and description that is an array of strings.
     * It is set up to be optional,
     * indexable (useful for searches) but the value does not need to be unique.
     * These are the typical values used for most open metadata attribute.
     * They can be changed by the caller once the TypeDefAttribute is returned.
     *
     * @param attributeName name of the attribute
     * @param attributeDescription short description for the attribute
     * @param attributeDescriptionGUID guid of the glossary term that describes this attribute.
     * @return  TypeDefAttribute of type string
     */
    public TypeDefAttribute  getArrayIntTypeDefAttribute(String      attributeName,
                                                         String      attributeDescription,
                                                         String      attributeDescriptionGUID)
    {
        TypeDefAttribute     attribute = new TypeDefAttribute();

        attribute.setAttributeName(attributeName);
        attribute.setAttributeDescription(attributeDescription);
        attribute.setAttributeDescriptionGUID(attributeDescriptionGUID);
        attribute.setAttributeType(this.archiveBuilder.getCollectionDef("array<int>"));
        attribute.setAttributeCardinality(AttributeCardinality.AT_MOST_ONE);
        attribute.setValuesMinCount(0);
        attribute.setValuesMaxCount(1);
        attribute.setIndexable(true);
        attribute.setUnique(false);
        attribute.setDefaultValue(null);
        attribute.setExternalStandardMappings(null);

        return attribute;
    }


    /**
     * Return an attribute with the supplied name and description that is of a map from string to string.
     * It is set up to be optional,
     * indexable (useful for searches) but the value does not need to be unique.
     * These are the typical values used for most open metadata attribute.
     * They can be changed by the caller once the TypeDefAttribute is returned.
     *
     * @param attributeName name of the attribute
     * @param attributeDescription short description for the attribute
     * @param attributeDescriptionGUID guid of the glossary term that describes this attribute.
     * @return  TypeDefAttribute of type string
     */
    public TypeDefAttribute  getMapStringStringTypeDefAttribute(String      attributeName,
                                                                String      attributeDescription,
                                                                String      attributeDescriptionGUID)
    {
        TypeDefAttribute     attribute = new TypeDefAttribute();

        attribute.setAttributeName(attributeName);
        attribute.setAttributeDescription(attributeDescription);
        attribute.setAttributeDescriptionGUID(attributeDescriptionGUID);
        attribute.setAttributeType(this.archiveBuilder.getCollectionDef("map<string,string>"));
        attribute.setAttributeCardinality(AttributeCardinality.AT_MOST_ONE);
        attribute.setValuesMinCount(0);
        attribute.setValuesMaxCount(1);
        attribute.setIndexable(true);
        attribute.setUnique(false);
        attribute.setDefaultValue(null);
        attribute.setExternalStandardMappings(null);

        return attribute;
    }


    /**
     * Return an attribute with the supplied name and description that is of a map from string to string.
     * It is set up to be optional,
     * indexable (useful for searches) but the value does not need to be unique.
     * These are the typical values used for most open metadata attribute.
     * They can be changed by the caller once the TypeDefAttribute is returned.
     *
     * @param attributeName name of the attribute
     * @param attributeDescription short description for the attribute
     * @param attributeDescriptionGUID guid of the glossary term that describes this attribute.
     * @return  TypeDefAttribute of type string
     */
    public TypeDefAttribute  getMapStringObjectTypeDefAttribute(String      attributeName,
                                                                String      attributeDescription,
                                                                String      attributeDescriptionGUID)
    {
        TypeDefAttribute     attribute = new TypeDefAttribute();

        attribute.setAttributeName(attributeName);
        attribute.setAttributeDescription(attributeDescription);
        attribute.setAttributeDescriptionGUID(attributeDescriptionGUID);
        attribute.setAttributeType(this.archiveBuilder.getCollectionDef("map<string,object>"));
        attribute.setAttributeCardinality(AttributeCardinality.AT_MOST_ONE);
        attribute.setValuesMinCount(0);
        attribute.setValuesMaxCount(1);
        attribute.setIndexable(true);
        attribute.setUnique(false);
        attribute.setDefaultValue(null);
        attribute.setExternalStandardMappings(null);

        return attribute;
    }


    /**
     * Return an attribute with the supplied name and description that is of a map from string to boolean.
     * It is set up to be optional,
     * indexable (useful for searches) but the value does not need to be unique.
     * These are the typical values used for most open metadata attribute.
     * They can be changed by the caller once the TypeDefAttribute is returned.
     *
     * @param attributeName name of the attribute
     * @param attributeDescription short description for the attribute
     * @param attributeDescriptionGUID guid of the glossary term that describes this attribute.
     * @return  TypeDefAttribute of type string
     */
    public TypeDefAttribute  getMapStringBooleanTypeDefAttribute(String      attributeName,
                                                                 String      attributeDescription,
                                                                 String      attributeDescriptionGUID)
    {
        TypeDefAttribute     attribute = new TypeDefAttribute();

        attribute.setAttributeName(attributeName);
        attribute.setAttributeDescription(attributeDescription);
        attribute.setAttributeDescriptionGUID(attributeDescriptionGUID);
        attribute.setAttributeType(this.archiveBuilder.getCollectionDef("map<string,boolean>"));
        attribute.setAttributeCardinality(AttributeCardinality.AT_MOST_ONE);
        attribute.setValuesMinCount(0);
        attribute.setValuesMaxCount(1);
        attribute.setIndexable(true);
        attribute.setUnique(false);
        attribute.setDefaultValue(null);
        attribute.setExternalStandardMappings(null);

        return attribute;
    }


    /**
     * Return an attribute with the supplied name and description that is of a map from string to boolean.
     * It is set up to be optional,
     * indexable (useful for searches) but the value does not need to be unique.
     * These are the typical values used for most open metadata attribute.
     * They can be changed by the caller once the TypeDefAttribute is returned.
     *
     * @param attributeName name of the attribute
     * @param attributeDescription short description for the attribute
     * @param attributeDescriptionGUID guid of the glossary term that describes this attribute.
     * @return  TypeDefAttribute of type string
     */
    public TypeDefAttribute  getMapStringLongTypeDefAttribute(String      attributeName,
                                                              String      attributeDescription,
                                                              String      attributeDescriptionGUID)
    {
        TypeDefAttribute     attribute = new TypeDefAttribute();

        attribute.setAttributeName(attributeName);
        attribute.setAttributeDescription(attributeDescription);
        attribute.setAttributeDescriptionGUID(attributeDescriptionGUID);
        attribute.setAttributeType(this.archiveBuilder.getCollectionDef("map<string,long>"));
        attribute.setAttributeCardinality(AttributeCardinality.AT_MOST_ONE);
        attribute.setValuesMinCount(0);
        attribute.setValuesMaxCount(1);
        attribute.setIndexable(true);
        attribute.setUnique(false);
        attribute.setDefaultValue(null);
        attribute.setExternalStandardMappings(null);

        return attribute;
    }


    /**
     * Return an attribute with the supplied name and description that is of a map from string to boolean.
     * It is set up to be optional,
     * indexable (useful for searches) but the value does not need to be unique.
     * These are the typical values used for most open metadata attribute.
     * They can be changed by the caller once the TypeDefAttribute is returned.
     *
     * @param attributeName name of the attribute
     * @param attributeDescription short description for the attribute
     * @param attributeDescriptionGUID guid of the glossary term that describes this attribute.
     * @return  TypeDefAttribute of type string
     */
    public TypeDefAttribute  getMapStringIntTypeDefAttribute(String      attributeName,
                                                             String      attributeDescription,
                                                             String      attributeDescriptionGUID)
    {
        TypeDefAttribute     attribute = new TypeDefAttribute();

        attribute.setAttributeName(attributeName);
        attribute.setAttributeDescription(attributeDescription);
        attribute.setAttributeDescriptionGUID(attributeDescriptionGUID);
        attribute.setAttributeType(this.archiveBuilder.getCollectionDef("map<string,int>"));
        attribute.setAttributeCardinality(AttributeCardinality.AT_MOST_ONE);
        attribute.setValuesMinCount(0);
        attribute.setValuesMaxCount(1);
        attribute.setIndexable(true);
        attribute.setUnique(false);
        attribute.setDefaultValue(null);
        attribute.setExternalStandardMappings(null);

        return attribute;
    }


    /**
     * Return an attribute with the supplied name, type and description.  It is set up to be optional,
     * indexable (useful for searches) but the value does not need to be unique.
     * These are the typical values used for most open metadata attribute.
     * They can be changed by the caller once the TypeDefAttribute is returned.
     *
     * @param enumTypeName name of the enum type for this attribute
     * @param attributeName name of the attribute
     * @param attributeDescription short description for the attribute
     * @param attributeDescriptionGUID guid of the glossary term that describes this attribute.
     * @return  TypeDefAttribute of type string
     */
    public TypeDefAttribute  getEnumTypeDefAttribute(String     enumTypeName,
                                                     String     attributeName,
                                                     String     attributeDescription,
                                                     String     attributeDescriptionGUID)
    {
        TypeDefAttribute     attribute = new TypeDefAttribute();

        attribute.setAttributeName(attributeName);
        attribute.setAttributeDescription(attributeDescription);
        attribute.setAttributeDescriptionGUID(attributeDescriptionGUID);
        attribute.setAttributeType(this.archiveBuilder.getEnumDef(enumTypeName));
        attribute.setAttributeCardinality(AttributeCardinality.AT_MOST_ONE);
        attribute.setValuesMinCount(0);
        attribute.setValuesMaxCount(1);
        attribute.setIndexable(true);
        attribute.setUnique(false);
        attribute.setDefaultValue(null);
        attribute.setExternalStandardMappings(null);

        return attribute;
    }


    /**
     * Return an attribute with the supplied name, type and description.  It is set up to be optional,
     * indexable (useful for searches) but the value does not need to be unique.
     * These are the typical values used for most open metadata attribute.
     * They can be changed by the caller once the TypeDefAttribute is returned.
     *
     * @param enumArrayTypeName name of the enum type for this attribute
     * @param attributeName name of the attribute
     * @param attributeDescription short description for the attribute
     * @param attributeDescriptionGUID guid of the glossary term that describes this attribute.
     * @return  TypeDefAttribute of type string
     */
    public TypeDefAttribute  getArrayEnumTypeDefAttribute(String     enumArrayTypeName,
                                                          String     attributeName,
                                                          String     attributeDescription,
                                                          String     attributeDescriptionGUID)
    {
        TypeDefAttribute     attribute = new TypeDefAttribute();

        attribute.setAttributeName(attributeName);
        attribute.setAttributeDescription(attributeDescription);
        attribute.setAttributeDescriptionGUID(attributeDescriptionGUID);
        attribute.setAttributeType(this.archiveBuilder.getCollectionDef(enumArrayTypeName));
        attribute.setAttributeCardinality(AttributeCardinality.AT_MOST_ONE);
        attribute.setValuesMinCount(0);
        attribute.setValuesMaxCount(1);
        attribute.setIndexable(true);
        attribute.setUnique(false);
        attribute.setDefaultValue(null);
        attribute.setExternalStandardMappings(null);

        return attribute;
    }


    /**
     * Returns a basic RelationshipDef without any attributes or ends set up.
     * The caller is responsible for adding the attributes and ends definition.
     *
     * @param guid unique identifier for the relationship
     * @param name name of the relationship
     * @param superType Super type for this relationship (null for top-level)
     * @param description short default description of the relationship
     * @param descriptionGUID guid of the glossary term that describes this relationship
     * @param propagationRule should classifications propagate over this relationship?
     * @return RelationshipDef with no ends defined.
     */
    public RelationshipDef getBasicRelationshipDef(String                        guid,
                                                   String                        name,
                                                   TypeDefLink                   superType,
                                                   String                        description,
                                                   String                        descriptionGUID,
                                                   ClassificationPropagationRule propagationRule)
    {
        RelationshipDef relationshipDef = new RelationshipDef();

        /*
         * Set up the parameters supplied by the caller.
         */
        relationshipDef.setGUID(guid);
        relationshipDef.setName(name);
        relationshipDef.setSuperType(superType);
        relationshipDef.setDescription(description);
        relationshipDef.setDescriptionGUID(descriptionGUID);

        /*
         * Set up the defaults
         */
        relationshipDef.setOrigin(archiveGUID);
        relationshipDef.setCreatedBy(originatorName);
        relationshipDef.setCreateTime(creationDate);
        relationshipDef.setVersion(versionNumber);
        relationshipDef.setVersionName(versionName);

        /*
         * Set default valid instance statuses
         */
        ArrayList<InstanceStatus>    validInstanceStatusList  = new ArrayList<>();
        validInstanceStatusList.add(InstanceStatus.ACTIVE);
        validInstanceStatusList.add(InstanceStatus.DELETED);
        relationshipDef.setValidInstanceStatusList(validInstanceStatusList);
        relationshipDef.setInitialStatus(InstanceStatus.ACTIVE);

        /*
         * Use the supplied propagation rule.
         */
        relationshipDef.setPropagationRule(propagationRule);

        return relationshipDef;
    }


    /**
     * Returns a RelationshipEndDef object that sets up details of an entity at one end of a relationship.
     *
     * @param entityType details of the type of entity connected to this end.
     * @param attributeName name of the attribute that the entity at the other end uses to refer to this entity.
     * @param attributeDescription description of this attribute
     * @param attributeDescriptionGUID unique identifier of the glossary term describing this attribute.
     * @param attributeCardinality cardinality of this end of the relationship.
     * @return the definition of one end of a Relationship.
     */
    public RelationshipEndDef  getRelationshipEndDef(TypeDefLink                entityType,
                                                     String                     attributeName,
                                                     String                     attributeDescription,
                                                     String                     attributeDescriptionGUID,
                                                     RelationshipEndCardinality attributeCardinality)
    {
        RelationshipEndDef  relationshipEndDef = new RelationshipEndDef();

        relationshipEndDef.setEntityType(entityType);
        relationshipEndDef.setAttributeName(attributeName);
        relationshipEndDef.setAttributeDescription(attributeDescription);
        relationshipEndDef.setAttributeDescriptionGUID(attributeDescriptionGUID);
        relationshipEndDef.setAttributeCardinality(attributeCardinality);

        return relationshipEndDef;
    }


    /**
     * Returns a basic ClassificationDef without any attributes.   The caller is responsible for adding the
     * attribute definitions.
     *
     * @param guid unique identifier for the classification
     * @param name name of the classification
     * @param superType Super type for this classification (null for top-level)
     * @param description short description of the classification
     * @param descriptionGUID unique identifier of the glossary term that describes this classification.
     * @param validEntityDef which entities can this classification be linked to.
     * @param propagatable can the classification propagate over relationships?
     * @return ClassificationDef with no attributes defined.
     */
    public ClassificationDef getClassificationDef(String                        guid,
                                                  String                        name,
                                                  TypeDefLink                   superType,
                                                  String                        description,
                                                  String                        descriptionGUID,
                                                  TypeDefLink                   validEntityDef,
                                                  boolean                       propagatable)
    {
        ClassificationDef classificationDef = new ClassificationDef();

        /*
         * Set up the parameters supplied by the caller.
         */
        classificationDef.setGUID(guid);
        classificationDef.setName(name);
        classificationDef.setSuperType(superType);
        classificationDef.setDescription(description);
        classificationDef.setDescriptionGUID(descriptionGUID);

        /*
         * Set up the defaults
         */
        classificationDef.setOrigin(archiveGUID);
        classificationDef.setCreatedBy(originatorName);
        classificationDef.setCreateTime(creationDate);
        classificationDef.setVersion(versionNumber);
        classificationDef.setVersionName(versionName);

        /*
         * Set default valid instance statuses
         */
        ArrayList<InstanceStatus>    validInstanceStatusList  = new ArrayList<>();
        validInstanceStatusList.add(InstanceStatus.ACTIVE);
        validInstanceStatusList.add(InstanceStatus.DELETED);
        classificationDef.setValidInstanceStatusList(validInstanceStatusList);
        classificationDef.setInitialStatus(InstanceStatus.ACTIVE);

        /*
         * Set up the supplied validEntityTypes and propagatable flag.
         */
        ArrayList<TypeDefLink>   validEntityDefs = new ArrayList<>();
        validEntityDefs.add(validEntityDef);
        classificationDef.setValidEntityDefs(validEntityDefs);
        classificationDef.setPropagatable(propagatable);

        return classificationDef;
    }


    /*
     * ----------------------------------------------
     * Working with instances
     * ----------------------------------------------
     */

    /**
     * Return the list of valid properties for the instance
     *
     * @param definedAttributes list of attributes defined for the typedef
     * @param currentList current list of properties extracted from the subtypes
     * @return accumulated list of properties.
     */
    public List<String>  getPropertiesList(List<TypeDefAttribute>  definedAttributes,
                                           List<String>            currentList)
    {
        List<String>   newList = currentList;

        if (newList == null)
        {
            newList = new ArrayList<>();
        }

        if (definedAttributes != null)
        {
            for (TypeDefAttribute  attribute : definedAttributes)
            {
                if (attribute != null)
                {
                    newList.add(attribute.getAttributeName());
                }
            }
        }

        if (newList.isEmpty())
        {
            return null;
        }

        return newList;
    }


    /**
     * Returns the instance type object with the fields that can be directly extracted from the
     * supplied type definition.  This leaves the super types and valid properties list that needs
     * to be able to loop through the super types.
     *
     * @param typeDef supplied typeDef.
     * @return new instance type object
     */
    public InstanceType getInstanceTypeHeader(TypeDef  typeDef)
    {
        InstanceType instanceType = null;

        if (typeDef != null)
        {
            instanceType = new InstanceType();

            instanceType.setTypeDefCategory(typeDef.getCategory());
            instanceType.setTypeDefGUID(typeDef.getGUID());
            instanceType.setTypeDefName(typeDef.getName());
            instanceType.setTypeDefVersion(typeDef.getVersion());
            instanceType.setTypeDefDescription(typeDef.getDescription());
            instanceType.setTypeDefDescriptionGUID(typeDef.getDescriptionGUID());
            instanceType.setValidStatusList(typeDef.getValidInstanceStatusList());
        }

        return instanceType;
    }


    /**
     * Set up the common fields for an entity or a relationship.
     *
     * @param instanceAuditHeader instance object to fill
     * @param type type of the object
     * @param status instance status
     */
    private void setInstanceAuditHeader(InstanceAuditHeader  instanceAuditHeader,
                                        InstanceType         type,
                                        InstanceStatus       status)
    {
        instanceAuditHeader.setCreatedBy(originatorName);
        instanceAuditHeader.setCreateTime(creationDate);
        instanceAuditHeader.setInstanceLicense(license);
        instanceAuditHeader.setInstanceProvenanceType(instanceProvenanceType);
        instanceAuditHeader.setMaintainedBy(null);
        instanceAuditHeader.setMetadataCollectionId(archiveGUID);
        instanceAuditHeader.setMetadataCollectionName(archiveName);
        instanceAuditHeader.setReplicatedBy(null);
        instanceAuditHeader.setStatus(InstanceStatus.ACTIVE);
        instanceAuditHeader.setStatusOnDelete(null);
        instanceAuditHeader.setType(type);
        instanceAuditHeader.setUpdatedBy(null);
        instanceAuditHeader.setUpdateTime(null);
        instanceAuditHeader.setVersion(1L);
        instanceAuditHeader.setStatus(status);
    }


    /**
     * Set up the common fields for an entity or a relationship.
     *
     * @param instanceHeader instance object to fill
     * @param type type of the object
     * @param guid unique identifier
     * @param status instance status
     */
    private void setInstanceHeader(InstanceHeader  instanceHeader,
                                   InstanceType    type,
                                   String          guid,
                                   InstanceStatus  status)
    {
        setInstanceAuditHeader(instanceHeader, type, status);

        instanceHeader.setGUID(guid);
        instanceHeader.setInstanceURL(null);
    }


    /**
     * Return a specific entity detail instance.
     *
     * @param type type of the entity
     * @param guid unique identifier of the entity
     * @param properties properties (attributes) for the entity
     * @param status instance status
     * @param classifications list of classifications
     * @return assembled entity
     */
    EntityDetail getEntityDetail(InstanceType          type,
                                 String                guid,
                                 InstanceProperties    properties,
                                 InstanceStatus        status,
                                 List<Classification>  classifications)
    {
        EntityDetail  entityDetail = new EntityDetail();

        this.setInstanceHeader(entityDetail, type, guid, status);
        entityDetail.setProperties(properties);
        entityDetail.setClassifications(classifications);

        return entityDetail;
    }


    /**
     * Return a specific relationship instance.
     *
     * @param type type of the relationship
     * @param guid unique identifier of the relationship
     * @param properties properties (attributes) for the relationship
     * @param status instance status
     * @param end1 relationship end 1
     * @param end2 relationship end 2
     * @return relationship instance
     */
    Relationship getRelationship(InstanceType          type,
                                 String                guid,
                                 InstanceProperties    properties,
                                 InstanceStatus        status,
                                 EntityProxy           end1,
                                 EntityProxy           end2)
    {
        Relationship  relationship = new Relationship();

        this.setInstanceHeader(relationship, type, guid, status);
        relationship.setProperties(properties);
        relationship.setEntityOneProxy(end1);
        relationship.setEntityTwoProxy(end2);

        return relationship;
    }


    /**
     * Return a specific classification instance.
     *
     * @param type type of the classification
     * @param properties properties (attributes) for the classification
     * @param status instance status
     * @return classification instance
     */
    Classification getClassification(InstanceType          type,
                                     InstanceProperties    properties,
                                     InstanceStatus        status)
    {
        Classification  classification = new Classification();

        this.setInstanceAuditHeader(classification, type, status);
        classification.setProperties(properties);

        return classification;
    }


    /**
     * Return a specific entity proxy instance.
     *
     * @param type type of the classification
     * @param guid unique identifier of the entity
     * @param properties unique properties (attributes) for the entity
     * @param status instance status
     * @param classifications list of classifications
     * @return classification instance
     */
    EntityProxy getEntityProxy(InstanceType          type,
                               String                guid,
                               InstanceProperties    properties,
                               InstanceStatus        status,
                               List<Classification>  classifications)
    {
        EntityProxy  entityProxy = new EntityProxy();

        this.setInstanceHeader(entityProxy, type, guid, status);
        entityProxy.setUniqueProperties(properties);
        entityProxy.setClassifications(classifications);

        return entityProxy;
    }
}
