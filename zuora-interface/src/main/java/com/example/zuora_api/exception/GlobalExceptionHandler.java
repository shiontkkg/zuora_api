package com.example.zuora_api.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ExternalApiException.class)
  public ResponseEntity<Map<String, Object>> handleZuoraApiException(ExternalApiException e) {
    var response = new HashMap<String, Object>();
    response.put("status", e.getStatus());
    response.put("error", HttpStatus.resolve(e.getStatus()).getReasonPhrase());
    response.put("message", e.getMessage());
    return ResponseEntity.status(e.getStatus()).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    response.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    response.put("message", "An unexpected error occurred: " + e.getMessage());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
