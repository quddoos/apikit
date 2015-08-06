/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.metadata.exception;

/**
 * 
 * @author arielsegura
 */
public class GatewayMetadataResourceNotFound extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7839109007316280213L;

	public GatewayMetadataResourceNotFound() {
		// TODO Auto-generated constructor stub
	}

	public GatewayMetadataResourceNotFound(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public GatewayMetadataResourceNotFound(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public GatewayMetadataResourceNotFound(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public GatewayMetadataResourceNotFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
