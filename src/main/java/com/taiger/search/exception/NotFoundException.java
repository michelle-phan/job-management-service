package com.taiger.search.exception;

/** Exception for not found API responses. */
public class NotFoundException extends BaseException {

  private static final long serialVersionUID = 1L;

  public NotFoundException() {
    super();
  }

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotFoundException(String message, String details) {
    super(message, details);
  }

  public NotFoundException(String message, String details, Throwable cause) {
    super(message, details, cause);
  }

  public NotFoundException(Throwable cause) {
    super(cause);
  }
}
