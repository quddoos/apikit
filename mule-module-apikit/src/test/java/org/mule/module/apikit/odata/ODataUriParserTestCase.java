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
	ODataRequestProcessor processor = ODataUriParser.parse(
		"odata.svc/$metadata", "");
	assertTrue(processor instanceof ODataMetadataProcessor);
    }

    @Test
    public void parseServiceDocumentRequestTrailingSlash()
	    throws ODataInvalidQueryRequestException {
	ODataRequestProcessor processor = ODataUriParser
		.parse("odata.svc/", "");
	assertTrue(processor instanceof ODataMetadataProcessor);
    }

    @Test
    public void parseServiceDocumentRequest()
	    throws ODataInvalidQueryRequestException {
	ODataRequestProcessor processor = ODataUriParser.parse("odata.svc", "");
	assertTrue(processor instanceof ODataServiceDocumentProcessor);
    }

    @Test
    public void parseEntityRequest() throws ODataInvalidQueryRequestException {
	ODataRequestProcessor processor = ODataUriParser.parse(
		"odata.svc/user", "");
	assertTrue(processor instanceof ODataApikitProcessor);
	ODataApikitProcessor apikitProcessor = (ODataApikitProcessor) processor;
	assertEquals("/user", apikitProcessor.getUri());
    }

    @Test
    public void parseEntityRequestWithId()
	    throws ODataInvalidQueryRequestException {
	ODataRequestProcessor processor = ODataUriParser.parse(
		"odata.svc/user(1)", "");
	assertTrue(processor instanceof ODataApikitProcessor);
	ODataApikitProcessor apikitProcessor = (ODataApikitProcessor) processor;
	assertEquals("/user/1", apikitProcessor.getUri());
    }

    @Test
    public void parseEntityRequestWithQuerystring()
	    throws ODataInvalidQueryRequestException {
	ODataRequestProcessor processor = ODataUriParser.parse(
		"odata.svc/user", "?$skip=1&$top=10");
	assertTrue(processor instanceof ODataApikitProcessor);
	ODataApikitProcessor apikitProcessor = (ODataApikitProcessor) processor;
	assertEquals("/user?skip=1&top=10", apikitProcessor.getUri());
    }

    @Test
    public void invalidParsingRequest() {
	try {
	    ODataUriParser.parse("odata.svc/$meTadata", "");
	    fail("Exception expected");
	} catch (Exception e) {
	    // Expected exception, doing nothing.
	}

	try {
	    ODataUriParser.parse("odata.svc$meTadata", "");
	    fail("Exception expected");
	} catch (Exception e) {
	    // Expected exception, doing nothing.
	}

	try {
	    ODataUriParser.parse("odata.svc/user/category", "");
	    fail("Exception expected");
	} catch (Exception e) {
	    // Expected exception, doing nothing.
	}

	try {
	    ODataUriParser.parse("odata.svc/users$top=1", "");
	    fail("Exception expected");
	} catch (Exception e) {
	    // Expected exception, doing nothing.
	}

	try {
	    ODataUriParser.parse("odata.svc", "?$top=1");
	    fail("Exception expected");
	} catch (Exception e) {
	    // Expected exception, doing nothing.
	}

	try {
	    ODataUriParser.parse("odata.svc", "?$topo=1");
	    fail("Exception expected");
	} catch (Exception e) {
	    // Expected exception, doing nothing.
	}
    }

}
