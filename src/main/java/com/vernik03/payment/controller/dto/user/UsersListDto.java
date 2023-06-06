package com.vernik03.payment.controller.dto.user;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersListDto {

  private List<UserWithoutAccountsDto> users;

}
