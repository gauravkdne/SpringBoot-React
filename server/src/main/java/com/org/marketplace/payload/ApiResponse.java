package com.org.marketplace.payload;

import java.util.List;

import org.springframework.http.HttpStatus;

/**
 * @author gauravkahadane
 *
 */
public class ApiResponse {

	private Boolean success;
	private HttpStatus status;
	private String message;
	private List<String> errors;

	public ApiResponse(Boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public ApiResponse(Boolean success, HttpStatus status, String message) {
		super();
		this.success = success;
		this.status = status;
		this.message = message;
	}

	public ApiResponse(Boolean success, HttpStatus status, String message, List<String> errors) {
		super();
		this.success = success;
		this.status = status;
		this.message = message;
		this.errors = errors;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
}
