package com.example.zuora_api.exception;

import lombok.Getter;

@Getter
public class ExternalApiException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final int status;

  public ExternalApiException(String message, int status) {
    super(message);
    this.status = status;
  }
}
