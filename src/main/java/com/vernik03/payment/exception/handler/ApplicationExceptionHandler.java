package com.vernik03.payment.exception.handler;

import com.vernik03.payment.exception.CommonException;
import com.vernik03.payment.exception.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApplicationExceptionHandler {

  @ExceptionHandler(CommonException.class)
  public ResponseEntity<ErrorMessage> handleCommonException(CommonException e) {
    ErrorMessage errorMessage = e.getErrorMessage();

    return errorMessage.toResponseEntity();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorMessage> handleValidationException(MethodArgumentNotValidException e) {
    String message = getValidationErrorMessage(e);
    String[] splitMessage = message.split(":");
    ErrorMessage errorMessage = new ErrorMessage(
        HttpStatus.BAD_REQUEST,
        splitMessage[0],
        splitMessage[1]
    );

    return errorMessage.toResponseEntity();
  }

  private String getValidationErrorMessage(MethodArgumentNotValidException e) {
    return e.getBindingResult().getFieldError().getDefaultMessage();
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ErrorMessage> handleUnhandledException() {
    ErrorMessage errorMessage = new ErrorMessage(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "internal_server_error",
        "An internal server error occurred"
    );

    return errorMessage.toResponseEntity();
  }

}
