/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata.exception;

public class ODataInvalidQueryRequestException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2615177524761296514L;

	public ODataInvalidQueryRequestException() {
		super("Query options $select, $expand, $filter, $orderby, $inlinecount, $skip, $skiptoken and $top are not supported by this request method or cannot be applied to the requested resource.");
		// TODO Auto-generated constructor stub
	}

	public ODataInvalidQueryRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public ODataInvalidQueryRequestException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ODataInvalidQueryRequestException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ODataInvalidQueryRequestException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
