/* SPDX-License-Identifier: Apache-2.0 */
package org.odpi.openmetadata.accessservices.assetlineage.util;

import java.util.HashMap;
import java.util.Map;

public final class Constants {

    private Constants() {
    }

    public static final String ASSET_LINEAGE_OMAS = "AssetLineageOmas";

    public static final String DERIVED_RELATIONAL_COLUMN = "DerivedRelationalColumn";
    public static final String DERIVED_SCHEMA_ATTRIBUTE = "DerivedSchemaAttribute";
    public static final String ASSET = "Asset";
    public static final String SCHEMA_ELEMENT = "SchemaElement";
    public static final String GLOSSARY_TERM = "GlossaryTerm";

    //Area 5 Types
    public static final String RELATIONAL_COLUMN = "RelationalColumn";
    public static final String RELATIONAL_TABLE = "RelationalTable";
    public static final String DEPLOYED_DB_SCHEMA_TYPE = "DeployedDatabaseSchema";
    public static final String DATA_STORE = "DataStore";


    public static final String PROCESS = "Process";
    public static final String PORT_ALIAS = "PortAlias";
    public static final String PORT_IMPLEMENTATION = "PortImplementation";
    public static final String TABULAR_SCHEMA_TYPE = "TabularSchemaType";
    public static final String TABULAR_COLUMN_TYPE = "TabularColumnType";
    public static final String TABULAR_COLUMN = "TabularColumn";
    public static final String DATA_FILE = "DataFile";

    //Relationships Type
    public static final String SCHEMA_ATTRIBUTE_TYPE = "SchemaAttributeType";
    public static final String SCHEMA_ATTRIBUTE = "SchemaAttribute";
    public static final String ATTRIBUTE_FOR_SCHEMA = "AttributeForSchema";
    public static final String COMPLEX_SCHEMA_TYPE = "ComplexSchemaType";
    public static final String ASSET_SCHEMA_TYPE = "AssetSchemaType";
    public static final String CONNECTION_TO_ASSET = "ConnectionToAsset";
    public static final String CONNECTION_CONNECTOR_TYPE = "ConnectionConnectorType";
    public static final String CONNECTION_ENDPOINT = "ConnectionEndpoint";
    public static final String DATA_CONTENT_FOR_DATA_SET = "DataContentForDataSet";
    public static final String SEMANTIC_ASSIGNMENT = "SemanticAssignment";
    public static final String TERM_CATEGORIZATION = "TermCategorization";
    public static final String PORT_DELEGATION = "PortDelegation";
    public static final String PROCESS_PORT = "ProcessPort";
    public static final String LINEAGE_MAPPING = "LineageMapping";
    public static final String SCHEMA_TYPE = "SchemaType";
    public static final String PORT_SCHEMA = "PortSchema";
    public static final String NESTED_FILE = "NestedFile";

    //Instance Properties fields
    public static final String DISPLAY_NAME = "displayName";
    public static final String AUTHOR = "author";
    public static final String VERSION_NUMBER = "versionNumber";
    public static final String OWNER = "owner";
    public static final String ENCODING_STANDARD = "encodingStandard";
    public static final String USAGE = "usage";
    public static final String DESCRIPTION = "description";
    public static final String NETWORK_ADDRESS = "networkAddress";
    public static final String PROTOCOL = "protocol";
    public static final String ENCRYPTION_METHOD = "encryptionMethod";
    public static final String CONNECTOR_PROVIDER_CLASS_NAME = "connectorProviderClassName";
    public static final String SECURED_PROPERTIES = "securedProperties";
    public static final String ADDITIONAL_PROPERTIES = "additionalProperties";
    public static final String TYPE = "dataType";
    public static final String QUALIFIED_NAME = "qualifiedName";
    public static final String NAME = "name";


    // Map of entities to relationship types
    public static final Map<String, String> processRelationshipsTypes = new HashMap<>();

    static {
        processRelationshipsTypes.put(PORT_ALIAS, PORT_DELEGATION);
        processRelationshipsTypes.put(PORT_IMPLEMENTATION, PORT_SCHEMA);
        processRelationshipsTypes.put(TABULAR_SCHEMA_TYPE, ATTRIBUTE_FOR_SCHEMA);
        processRelationshipsTypes.put(SCHEMA_ATTRIBUTE_TYPE, SCHEMA_ATTRIBUTE_TYPE);
        processRelationshipsTypes.put(TABULAR_COLUMN_TYPE, LINEAGE_MAPPING);
    }
}