package com.vernik03.payment.exception;

import lombok.Getter;

@Getter
public abstract class CommonException extends RuntimeException {

  private final ErrorMessage errorMessage;

  protected CommonException(ErrorMessage errorMessage) {
    super(errorMessage.getErrorDescription());
    this.errorMessage = errorMessage;
  }

}
