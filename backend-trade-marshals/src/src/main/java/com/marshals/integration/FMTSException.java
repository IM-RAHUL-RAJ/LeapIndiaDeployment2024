package com.marshals.integration;

public class FMTSException extends RuntimeException {

	private static final long serialVersionUID = 2L;

	public FMTSException() {
	}

	public FMTSException(String message) {
		super(message);
	}

	public FMTSException(Throwable cause) {
		super(cause);
	}

	public FMTSException(String message, Throwable cause) {
		super(message, cause);
	}

	protected FMTSException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
