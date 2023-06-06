package com.vernik03.payment.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@AllArgsConstructor
public class ErrorMessage {

  @JsonIgnore
  private final HttpStatus responseCode;

  private final String errorId;
  private final String errorDescription;

  public ResponseEntity<ErrorMessage> toResponseEntity() {
    return new ResponseEntity<>(this, responseCode);
  }

}
