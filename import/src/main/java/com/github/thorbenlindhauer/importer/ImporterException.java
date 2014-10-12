package com.github.thorbenlindhauer.importer;

public class ImporterException extends RuntimeException {
  
  private static final long serialVersionUID = 1L;
  
  public ImporterException(String message, Exception cause) {
    super(message, cause);
  }
  
  public ImporterException(String message) {
    super(message);
  }

}
