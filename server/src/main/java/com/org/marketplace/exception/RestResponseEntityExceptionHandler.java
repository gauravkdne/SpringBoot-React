/**
 * 
 */
package com.org.marketplace.exception;

import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.org.marketplace.payload.ApiError;

/**
 * REST response exception handler
 * 
 * @author gauravkahadane
 *
 */
@ControllerAdvice
@RestController
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

	public RestResponseEntityExceptionHandler() {
		super();
	}

	/**
	 * Handler for HTTP 400 error
	 */
	@Override
	protected final ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		LOGGER.info("Bad Request: {}", ex.getMessage());
		LOGGER.debug("Bad Request: ", ex);

		final ApiError apiError = message(HttpStatus.BAD_REQUEST, ex);
		return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
	}

	@Override
	protected final ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		LOGGER.info("Bad Request: {}", ex.getMessage());
		LOGGER.debug("Bad Request: ", ex);

		final BindingResult result = ex.getBindingResult();
		final List<FieldError> fieldErrors = result.getFieldErrors();
		final ValidationError dto = processFieldErrors(fieldErrors);

		return handleExceptionInternal(ex, dto, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	
	@ExceptionHandler(value = { MethodArgumentTypeMismatchException.class })
	protected final ResponseEntity<Object> handleMethodArgumentMissMatch(final RuntimeException ex, final WebRequest request) {
		LOGGER.info("Bad Request: {}", ex.getMessage());
		LOGGER.debug("Bad Request: ", ex);

		final ApiError apiError = message(HttpStatus.BAD_REQUEST, ex);

		return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(value = { ConstraintViolationException.class, DataIntegrityViolationException.class })
	public final ResponseEntity<Object> handleBadRequest(final RuntimeException ex, final WebRequest request) {
		LOGGER.info("Bad Request: {}", ex.getLocalizedMessage());
		LOGGER.debug("Bad Request: ", ex);

		final ApiError apiError = message(HttpStatus.BAD_REQUEST, ex);
		return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	/**
	 * Handler for HTTP 500 error
	 */
	@ExceptionHandler({ NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class,
			Exception.class })
	public ResponseEntity<Object> handle500s(final RuntimeException ex, final WebRequest request) {
		LOGGER.error("500 Status Code", ex);

		final ApiError apiError = message(HttpStatus.INTERNAL_SERVER_ERROR, ex);

		return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}
	
	/**
	 * Handler for HTTP 403 error
	 */
	@ExceptionHandler({ AccessDeniedException.class })
	public ResponseEntity<Object> handleEverything(final AccessDeniedException ex, final WebRequest request) {
		LOGGER.error("403 Status Code", ex);

		final ApiError apiError = message(HttpStatus.FORBIDDEN, ex);

		return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
	}

	/**
	 * Handler for HTTP 404 error
	 */
	@ExceptionHandler({ EntityNotFoundException.class, MyResourceNotFoundException.class })
	protected ResponseEntity<Object> handleNotFound(final RuntimeException ex, final WebRequest request) {
		LOGGER.warn("Not Found: {}", ex.getMessage());

		final ApiError apiError = message(HttpStatus.NOT_FOUND, ex);
		return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

	/**
	 * Handler for HTTP 409 error
	 */
	@ExceptionHandler({ InvalidDataAccessApiUsageException.class, DataAccessException.class })
	protected ResponseEntity<Object> handleConflict(final RuntimeException ex, final WebRequest request) {
		LOGGER.warn("Conflict: {}", ex.getMessage());

		final ApiError apiError = message(HttpStatus.CONFLICT, ex);
		return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

	/**
	 * Handler for HTTP 415 error
	 */
	@ExceptionHandler({ InvalidMimeTypeException.class, InvalidMediaTypeException.class })
	protected ResponseEntity<Object> handleInvalidMimeTypeException(final IllegalArgumentException ex,
			final WebRequest request) {
		LOGGER.warn("Unsupported Media Type: {}", ex.getMessage());

		final ApiError apiError = message(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex);
		return handleExceptionInternal(ex, apiError, new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
	}

	private ValidationError processFieldErrors(final List<FieldError> fieldErrors) {
		final ValidationError dto = new ValidationError();

		for (final FieldError fieldError : fieldErrors) {
			final String localizedErrorMessage = fieldError.getDefaultMessage();
			dto.addFieldError(fieldError.getField(), localizedErrorMessage);
		}

		return dto;
	}

	private ApiError message(final HttpStatus httpStatus, final Exception ex) {
		final String message = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
		final String devMessage = ex.getClass().getSimpleName();

		return new ApiError(httpStatus.value(), message, devMessage);
	}
}