/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.webservice.ws;

import org.mule.api.processor.MessageProcessor;
import org.mule.webservice.AbstractWebServiceInterface;

public class WSWebServiceInterface extends AbstractWebServiceInterface
{

    protected WSOperationResolutionMode operationResolutionMode = WSOperationResolutionMode.SOAP_ACTION;

    public WSWebServiceInterface(String name)
    {
        super(name);
    }

    @Override
    public MessageProcessor getOperationRouter()
    {
        if (operationResolutionMode == WSOperationResolutionMode.SOAP_ACTION)
        {
            return new SOAPActionOperationRouter(this);
        }
        else if (operationResolutionMode == WSOperationResolutionMode.PATH)
        {
            return null;
        }
        else
        {
            return null;
        }
    }
}