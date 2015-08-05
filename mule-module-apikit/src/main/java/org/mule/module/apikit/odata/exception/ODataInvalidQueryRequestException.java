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
