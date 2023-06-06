package com.vernik03.payment.controller.dto.user;

import com.vernik03.payment.controller.dto.bankaccount.BankAccountsWithoutUserDto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

  private Long id;
  private String name;
  private Boolean is_admin;
  private String login;
  private String password;
  private List<BankAccountsWithoutUserDto> bank_accounts;

}
