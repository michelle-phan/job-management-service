package com.taiger.search.exception;

/**
 * Base exception for API errors
 */
public abstract class BaseException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private String details;

    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(String message, String details) {
        super(message);
        this.details = details;
    }

    public BaseException(String message, String details, Throwable cause) {
        super(message, cause);
        this.details = details;
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    public String getDetails() {
        return details;
    }

}
