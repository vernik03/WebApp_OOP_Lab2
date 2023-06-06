package com.vernik03.payment.controller.dto.user;

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
public class UserForm {

  @NotBlank(message = "name_is_blank:Name can not be blank")
  @Size(max = 255, message = "name_max_size_limit:Max size of name is 255 characters")
  private String name;

  @NotNull(message = "is_admin_is_empty:Admin info can not be empty")
  private Boolean is_admin;

  @NotBlank(message = "login_is_empty:Login can not be empty")
  @Size(max = 255, message = "login_max_size_limit:Max size of login is 255 characters")
  private String login;

  @NotBlank(message = "password_is_empty:Password can not be empty")
  @Size(max = 255, message = "password_max_size_limit:Max size of password is 255 characters")
  private String password;

}
