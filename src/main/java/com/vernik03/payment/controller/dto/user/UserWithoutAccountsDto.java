package com.vernik03.payment.controller.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserWithoutAccountsDto {

  private Long id;
  private String name;
  private Boolean is_admin;
  private String login;
  private String password;

}
