package com.vernik03.payment.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CommonException {

  public static final ErrorMessage USER_NOT_FOUND = new ErrorMessage(
      HttpStatus.NOT_FOUND,
      "crew_member_not_found",
      "Crew member with this id not found"
  );

  public static final ErrorMessage BANK_ACCOUNT_NOT_FOUND = new ErrorMessage(
      HttpStatus.NOT_FOUND,
      "flight_not_found",
      "Flight with this id not found"
  );

  public static final ErrorMessage USER_NOT_FOUND_TO_LINK_UP = new ErrorMessage(
      HttpStatus.NOT_FOUND,
      "crew_member_not_found_to_link_up",
      "Unable to link up due crew member absence"
  );

  public static final ErrorMessage BANK_ACCOUNT_NOT_FOUND_TO_LINK_UP = new ErrorMessage(
      HttpStatus.NOT_FOUND,
      "flight_not_found_to_link_up",
      "Unable to link up due flight absence"
  );

  public static final ErrorMessage USER_NOT_FOUND_TO_UNLINK_UP = new ErrorMessage(
      HttpStatus.NOT_FOUND,
      "crew_member_not_found_to_unlink_up",
      "Unable to unlink up due crew member absence"
  );

  public static final ErrorMessage BANK_ACCOUNT_NOT_FOUND_TO_UNLINK_UP = new ErrorMessage(
      HttpStatus.NOT_FOUND,
      "flight_not_found_to_unlink_up",
      "Unable to unlink up due flight absence"
  );

  public static final ErrorMessage LINK_IS_ABSENT = new ErrorMessage(
      HttpStatus.NOT_FOUND,
      "link_is_absent",
      "The link between these crew member and flight is absent"
  );

  public NotFoundException(ErrorMessage errorMessage) {
    super(errorMessage);
  }

}
