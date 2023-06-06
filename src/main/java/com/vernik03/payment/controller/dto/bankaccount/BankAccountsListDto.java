package com.vernik03.payment.controller.dto.bankaccount;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountsListDto {

  private List<BankAccountsWithoutUserDto> bank_accounts;

}
