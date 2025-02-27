/* SPDX-License-Identifier: Apache 2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.assetlineage.server;


import org.odpi.openmetadata.accessservices.assetlineage.admin.AssetLineageAdmin;

import org.odpi.openmetadata.adminservices.configuration.OMAGAccessServiceRegistration;
import org.odpi.openmetadata.adminservices.configuration.registration.AccessServiceDescription;
import org.odpi.openmetadata.adminservices.configuration.registration.AccessServiceOperationalStatus;
import org.odpi.openmetadata.adminservices.configuration.registration.AccessServiceRegistration;

/**
 * AssetLineageRegistration registers the access service with the OMAG Server administration services.
 * This registration must be driven once at server start up.  The OMAG Server administration services
 * then use this registration information as confirmation that there is an implementation of this
 * access service in the server and it can be configured and used.
 */
class AssetLineageRegistration
{
    /**
     * Pass information about this access service to the OMAG Server administration services.
     */
    static void registerAccessService()
    {
        AccessServiceDescription myDescription = AccessServiceDescription.ASSET_LINEAGE_OMAS;

        AccessServiceRegistration myRegistration = new AccessServiceRegistration(myDescription,
                AccessServiceOperationalStatus.ENABLED,
                AssetLineageAdmin.class.getName());
        OMAGAccessServiceRegistration.registerAccessService(myRegistration);
    }
}
