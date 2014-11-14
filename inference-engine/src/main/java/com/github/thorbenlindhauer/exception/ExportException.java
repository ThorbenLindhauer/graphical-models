package com.github.thorbenlindhauer.exception;

public class ExportException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ExportException(String message, Throwable cause) {
    super(message, cause);
  }

  public ExportException(String message) {
    super(message);
  }
}
