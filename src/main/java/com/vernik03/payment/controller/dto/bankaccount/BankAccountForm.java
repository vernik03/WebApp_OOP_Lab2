package com.vernik03.payment.controller.dto.bankaccount;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountForm {

  @NotBlank(message = "card_number_is_blank:Card numbe can not be blank")
  @Size(max = 16, message = "card_number_max_size_limit:Card number max size is 16 characters")
  private String card_number;

  @NotNull(message = "balance_is_blank:Balance can not be blank")
  private Double balance;

  @NotNull(message = "is_blocked_is_empty:Blocked info can not be empty")
  private Boolean is_blocked;

}
