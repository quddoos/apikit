/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.apikit.odata;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mule.module.apikit.odata.exception.ODataInvalidQueryRequestException;
import org.mule.module.apikit.odata.processor.ODataApikitProcessor;
import org.mule.module.apikit.odata.processor.ODataMetadataProcessor;
import org.mule.module.apikit.odata.processor.ODataRequestProcessor;
import org.mule.module.apikit.odata.processor.ODataServiceDocumentProcessor;

public class ODataUriParserTestCase {

	@Test
	public void parseMetadataRequest() throws ODataInvalidQueryRequestException {
		ODataRequestProcessor processor = ODataUriParser.parse(null, "/odata.svc/$metadata", "");
		assertTrue(processor instanceof ODataMetadataProcessor);
	}

	@Test
	public void parseServiceDocumentRequestTrailingSlash() throws ODataInvalidQueryRequestException {
		ODataRequestProcessor processor = ODataUriParser.parse(null, "/odata.svc/", "");
		assertTrue(processor instanceof ODataServiceDocumentProcessor);
	}

	@Test
	public void parseServiceDocumentRequest() throws ODataInvalidQueryRequestException {
		ODataRequestProcessor processor = ODataUriParser.parse(null, "/odata.svc", "");
		assertTrue(processor instanceof ODataServiceDocumentProcessor);
	}

	@Test
	public void parseEntityRequest() throws ODataInvalidQueryRequestException {
		ODataRequestProcessor processor = ODataUriParser.parse(null, "/odata.svc/user", "");
		assertTrue(processor instanceof ODataApikitProcessor);
		ODataApikitProcessor apikitProcessor = (ODataApikitProcessor) processor;
		assertEquals("/user", apikitProcessor.getPath());
	}

	@Test
	public void parseEntityRequestWithId() throws ODataInvalidQueryRequestException {
		ODataRequestProcessor processor = ODataUriParser.parse(null, "/odata.svc/user(1)", "");
		assertTrue(processor instanceof ODataApikitProcessor);
		ODataApikitProcessor apikitProcessor = (ODataApikitProcessor) processor;
		assertEquals("/user/1", apikitProcessor.getPath());
	}

	@Test
	public void parseEntityRequestWithQuerystring() throws ODataInvalidQueryRequestException {
		ODataRequestProcessor processor = ODataUriParser.parse(null, "/odata.svc/user", "$skip=1&$top=10");
		assertTrue(processor instanceof ODataApikitProcessor);
		ODataApikitProcessor apikitProcessor = (ODataApikitProcessor) processor;
		assertEquals("/user", apikitProcessor.getPath());
		assertEquals("skip=1&top=10", apikitProcessor.getQuery());
	}

	@Test
	public void invalidParsingRequest() {
		try {
			ODataUriParser.parse(null, "/odata.svc/$meTadata", "");
			fail("Exception expected");
		} catch (Exception e) {
			// Expected exception, doing nothing.
		}

		try {
			ODataUriParser.parse(null, "/odata.svc$meTadata", "");
			fail("Exception expected");
		} catch (Exception e) {
			// Expected exception, doing nothing.
		}

		try {
			ODataUriParser.parse(null, "/odata.svc/user/category", "");
			fail("Exception expected");
		} catch (Exception e) {
			// Expected exception, doing nothing.
		}

		try {
			ODataUriParser.parse(null, "/odata.svc/users$top=1", "");
			fail("Exception expected");
		} catch (Exception e) {
			// Expected exception, doing nothing.
		}

		try {
			ODataUriParser.parse(null, "/odata.svc", "$top=1");
			fail("Exception expected");
		} catch (Exception e) {
			// Expected exception, doing nothing.
		}

		try {
			ODataUriParser.parse(null, "/odata.svc", "$topo=1");
			fail("Exception expected");
		} catch (Exception e) {
			// Expected exception, doing nothing.
		}
	}

}
