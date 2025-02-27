/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.userinterface.accessservices.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.odpi.openmetadata.accessservices.assetcatalog.client.AssetCatalog;
import org.odpi.openmetadata.accessservices.assetcatalog.model.Asset;
import org.odpi.openmetadata.accessservices.assetcatalog.model.AssetDescription;
import org.odpi.openmetadata.accessservices.assetcatalog.model.Classification;
import org.odpi.openmetadata.accessservices.assetcatalog.model.Relationship;
import org.odpi.openmetadata.accessservices.assetcatalog.model.rest.responses.AssetDescriptionResponse;
import org.odpi.openmetadata.accessservices.assetcatalog.model.rest.responses.ClassificationsResponse;
import org.odpi.openmetadata.accessservices.assetcatalog.model.rest.responses.RelationshipsResponse;
import org.odpi.openmetadata.commonservices.ffdc.exceptions.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class AssetCatalogOMASServiceTest {

    private final String COMPLEX_SCHEMA_TYPE = "ComplexSchemaType";
    private final String assetId = "6662c0f2.e1b1ec6c.54865omh1.pco9ecb.c3g5f1.pfvf6bdv95dnc67jq2jli";
    private final String schemaId = "abababa1.e1b1ec6c.54865omh1.pco9ecb.c3g5f1.pfvf6bdv95dnc67jq2jli";
    private final String classificationDescription = "A uniquely identifying relational column.";
    private final String typeDef = "Asset";
    private final String relationshipTypeDef = "AssetSchemaType";
    private final String typeDefDescription = "A semantic description of something, such as a concept, object, asset, technology, role or group.";
    private final String relationshipGuid = "815b004d-73c6-4728-9dd9-536f4fe803cd";
    private final String user = "demo";

    @Mock
    private AssetCatalog assetCatalog;

    @InjectMocks
    private AssetCatalogOMASService assetCatalogOMASService;

    @Test
    @DisplayName("Asset Details")
    public void testGetAssetDetails() throws PropertyServerException, InvalidParameterException {
        AssetDescriptionResponse expectedResponse = mockAssetDescriptionResponse();
        when(assetCatalog.getAssetDetails(anyString(), anyString(), anyString())).thenReturn(expectedResponse);
        List<AssetDescription> resultList = assetCatalogOMASService.getAssetDetails(user, assetId, typeDef);
        verifyAssetDescriptionResult(resultList);
    }

    @Test
    @DisplayName("Asset Universe")
    public void testGetAssetUniverse() throws PropertyServerException, InvalidParameterException {
        AssetDescriptionResponse expectedResponse = mockAssetDescriptionResponse();
        when(assetCatalog.getAssetUniverse(anyString(), anyString(), anyString())).thenReturn(expectedResponse);
        List<AssetDescription> resultList = assetCatalogOMASService.getAssetUniverse(user, assetId, typeDef);
        verifyAssetDescriptionResult(resultList);
    }

    @Test
    @DisplayName("Asset Relationships by type")
    public void testGetAssetRelationships() throws PropertyServerException, InvalidParameterException {
        RelationshipsResponse expectedResponse = mockRelationshipResponse();
        when(assetCatalog.getAssetRelationships(anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(expectedResponse);
        List<Relationship> resultList = assetCatalogOMASService.getAssetRelationships(user, assetId, typeDef, relationshipTypeDef, 0, 1);
        verifyRelationshipResponse(resultList);
    }

    @Test
    @DisplayName("Asset Classification")
    public void testGetClassificationForAsset() throws PropertyServerException, InvalidParameterException {
        ClassificationsResponse expectedResponse = mockClassificationsResponse();
        when(assetCatalog.getClassificationsForAsset(anyString(), anyString(), anyString(), anyString())).thenReturn(expectedResponse);
        List<Classification> resultList = assetCatalogOMASService.getClassificationsForAsset(user, assetId, typeDef, "Confidentiality");
        verifyClassificationResponse(resultList);
    }

//    @Test()
//    @DisplayName("Asset Summary - exception")
//    public void testGetAssetSummaryException() throws PropertyServerException, InvalidParameterException {
//        InvalidParameterException mockedException = mockExceptionResponse("unit-test");
//        when(assetCatalog.getAssetSummary(anyString(), anyString())).thenThrow(mockedException);
//        InvalidParameterException thrown = assertThrows(InvalidParameterException.class,
//                () -> assetCatalogOMASService.getAssetSummary(user, "asset-id-does-not-exists"));
//        assertTrue(thrown.getMessage().contains("OMAS-ASSET-CATALOG-400-002"));
//
//    }
//
//    @Test
//    @DisplayName("Asset Details - exception")
//    public void testGetAssetDetailsException() throws PropertyServerException, InvalidParameterException {
//        InvalidParameterException mockedException = mockExceptionResponse("unit-test");
//        when(assetCatalog.getAssetDetails(anyString(), anyString())).thenThrow(mockedException);
//        InvalidParameterException thrown = assertThrows(InvalidParameterException.class,
//                () -> assetCatalogOMASService.getAssetDetails(user, "asset-id-does-not-exists"));
//        assertTrue(thrown.getMessage().contains("OMAS-ASSET-CATALOG-400-002"));
//    }
//
//    @Test
//    @DisplayName("Asset Universe - exception")
//    public void testGetAssetUniverseException() throws PropertyServerException, InvalidParameterException {
//        InvalidParameterException mockedException = mockExceptionResponse("unit-test");
//        when(assetCatalog.getAssetUniverse(, anyString(), anyString())).thenThrow(mockedException);
//        InvalidParameterException thrown = assertThrows(InvalidParameterException.class,
//                () -> assetCatalogOMASService.getAssetUniverse(user, "asset-id-does-not-exists"));
//        assertTrue(thrown.getMessage().contains("OMAS-ASSET-CATALOG-400-002"));
//    }
//
//    @Test
//    @DisplayName("Asset Relationships - exception")
//    public void testGetAssetRelationshipsException() throws PropertyServerException, InvalidParameterException {
//        InvalidParameterException mockedException = mockExceptionResponse("unit-test");
//        when(assetCatalog.getAssetRelationships(anyString(), anyString())).thenThrow(mockedException);
//        InvalidParameterException thrown = assertThrows(InvalidParameterException.class,
//                () -> assetCatalogOMASService.getAssetRelationships(user, "asset-id-does-not-exists"));
//        assertTrue(thrown.getMessage().contains("OMAS-ASSET-CATALOG-400-002"));
//    }
//
//    @Test
//    @DisplayName("Asset Relationships for type - exception")
//    public void testGetAssetRelationshipsForTypeException() throws PropertyServerException, InvalidParameterException {
//        InvalidParameterException mockedException = mockExceptionResponse("unit-test");
//        when(assetCatalog.getAssetRelationshipsForType(anyString(), anyString(), anyString())).thenThrow(mockedException);
//        InvalidParameterException thrown = assertThrows(InvalidParameterException.class,
//                () -> assetCatalogOMASService.getAssetRelationshipsForType(
//                        user, "asset-id-does-not-exists",
//                        "relationship-type-does-not-exists"));
//        assertTrue(thrown.getMessage().contains("OMAS-ASSET-CATALOG-400-002"));
//    }
//
//    @Test
//    @DisplayName("Asset Classification - exception")
//    public void testGetAssetClassificationException() throws PropertyServerException, InvalidParameterException {
//        InvalidParameterException mockedException = mockExceptionResponse("unit-test");
//        when(assetCatalog.getClassificationsForAsset(anyString(), anyString())).thenThrow(mockedException);
//        InvalidParameterException thrown = assertThrows(InvalidParameterException.class, ()
//                -> assetCatalogOMASService.getClassificationsForAsset(user, "asset-id-does-not-exists"));
//        assertTrue(thrown.getMessage().contains("OMAS-ASSET-CATALOG-400-002"));
//    }

    private AssetDescriptionResponse mockAssetDescriptionResponse() {
        AssetDescriptionResponse expectedResponse = new AssetDescriptionResponse();
        List<AssetDescription> expectedDescriptionList = new ArrayList<>();
        AssetDescription expectedDescription = new AssetDescription();
        expectedDescription.setGuid(assetId);
        expectedDescription.setTypeDefName(typeDef);
        expectedDescription.setTypeDefDescription(typeDefDescription);
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("summary", "Short description of term First Name");
        propertiesMap.put("displayName", "First Name");
        expectedDescription.setProperties(propertiesMap);
        expectedDescriptionList.add(expectedDescription);
        expectedResponse.setAssetDescriptionList(expectedDescriptionList);
        return expectedResponse;
    }

    private RelationshipsResponse mockRelationshipResponse() {
        RelationshipsResponse expectedResponse = new RelationshipsResponse();

        List<Relationship> expectedRelationshipList = new ArrayList<>();
        Relationship expectedRelationship = new Relationship();

        Asset fromEntity = new Asset();
        fromEntity.setGuid(assetId);
        fromEntity.setTypeDefName(typeDef);
        expectedRelationship.setFromEntity(fromEntity);

        Asset toEntity = new Asset();
        expectedRelationship.setToEntity(toEntity);
        toEntity.setGuid(schemaId);
        toEntity.setTypeDefName("ComplexSchemaType");

        expectedRelationshipList.add(expectedRelationship);
        expectedResponse.setRelationships(expectedRelationshipList);
        return expectedResponse;
    }

    private ClassificationsResponse mockClassificationsResponse() {
        ClassificationsResponse expectedResponse = new ClassificationsResponse();
        List<Classification> expectedClassificationList = new ArrayList<>();
        Classification expectedClassification = new Classification();
        expectedClassification.setTypeDefName("Confidentiality");
        expectedClassificationList.add(expectedClassification);
        expectedResponse.setClassifications(expectedClassificationList);
        return expectedResponse;
    }

    private void verifyAssetDescriptionResult(List<AssetDescription> resultList) {
        assertFalse(resultList.isEmpty());
        AssetDescription assetDescription = resultList.get(0);
        assertEquals(assetDescription.getGuid(), assetId);
        assertEquals(assetDescription.getTypeDefDescription(), typeDefDescription);
        assertFalse(assetDescription.getProperties().isEmpty());
    }

    private void verifyRelationshipResponse(List<Relationship> resultList) {
        assertFalse(resultList.isEmpty());
        Relationship relationship = resultList.get(0);
        assertEquals(relationship.getFromEntity().getGuid(), assetId);
        assertEquals(relationship.getFromEntity().getTypeDefName(), typeDef);
        assertEquals(relationship.getToEntity().getGuid(), schemaId);
        assertEquals(relationship.getToEntity().getTypeDefName(), COMPLEX_SCHEMA_TYPE);
    }

    private void verifyClassificationResponse(List<Classification> resultList) {
        assertFalse(resultList.isEmpty());
        Classification classification = resultList.get(0);
        assertEquals(classification.getTypeDefName(), "Confidentiality");
    }
}