package com.vernik03.payment.exception;

import org.springframework.http.HttpStatus;

public class ValidException extends CommonException {

  public static final ErrorMessage INVALID_PASSWORD = new ErrorMessage(
      HttpStatus.BAD_REQUEST,
      "invalid_password",
      "Wrong password"
  );

    public static final ErrorMessage INVALID_EMAIL = new ErrorMessage(
        HttpStatus.BAD_REQUEST,
        "invalid_email",
        "Wrong email"
    );

  public ValidException(ErrorMessage errorMessage) {
    super(errorMessage);
  }

}
