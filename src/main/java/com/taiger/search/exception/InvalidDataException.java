package com.taiger.search.exception;

/** Exception for invalid data. */
public class InvalidDataException extends BaseException {

  private static final long serialVersionUID = 1L;

  public InvalidDataException() {
    super();
  }

  public InvalidDataException(String message) {
    super(message);
  }

  public InvalidDataException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidDataException(String message, String details) {
    super(message, details);
  }

  public InvalidDataException(String message, String details, Throwable cause) {
    super(message, details, cause);
  }

  public InvalidDataException(Throwable cause) {
    super(cause);
  }
}
