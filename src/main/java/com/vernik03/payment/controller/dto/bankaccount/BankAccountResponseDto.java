package com.vernik03.payment.controller.dto.bankaccount;

import com.vernik03.payment.controller.dto.user.UserWithoutAccountsDto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountResponseDto {

  private Long id;
  private String cardNumber;
  private Double balance;
  private Boolean isBlocked;
  private List<UserWithoutAccountsDto> users;

}
