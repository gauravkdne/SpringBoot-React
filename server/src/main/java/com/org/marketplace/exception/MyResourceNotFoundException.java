package com.org.marketplace.exception;

public final class MyResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 7068651388138399939L;

	public MyResourceNotFoundException() {
		super();
	}

	public MyResourceNotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MyResourceNotFoundException(final String message) {
		super(message);
	}

	public MyResourceNotFoundException(final Throwable cause) {
		super(cause);
	}

}
