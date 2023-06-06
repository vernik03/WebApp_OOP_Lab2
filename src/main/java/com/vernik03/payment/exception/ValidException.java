package com.vernik03.payment.exception;

import org.springframework.http.HttpStatus;

public class ValidException extends CommonException {

  public static final ErrorMessage LINK_ALREADY_EXISTS = new ErrorMessage(
      HttpStatus.BAD_REQUEST,
      "link_already_exists",
      "Crew member and flight already linked up"
  );

  public ValidException(ErrorMessage errorMessage) {
    super(errorMessage);
  }

}
