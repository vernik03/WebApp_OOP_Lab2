package com.vernik03.payment.controller.dto.bankaccount;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountsWithoutUserDto {

  private Long id;
  private String card_number;
  private Double balance;
  private Boolean is_blocked;

}
