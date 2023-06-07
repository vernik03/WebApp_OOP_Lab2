package com.vernik03.payment.controller;

import com.vernik03.payment.controller.dto.user.UserForm;
import com.vernik03.payment.controller.dto.user.UserResponseDto;
import com.vernik03.payment.controller.dto.user.UserWithoutAccountsDto;
import com.vernik03.payment.controller.dto.user.UsersListDto;
import com.vernik03.payment.controller.dto.bankaccount.BankAccountsWithoutUserDto;
import com.vernik03.payment.exception.NotFoundException;
import com.vernik03.payment.model.User;
import com.vernik03.payment.model.BankAccount;
import com.vernik03.payment.service.BankAccountService;
import com.vernik03.payment.service.UserService;
import com.vernik03.payment.security.LogIn;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private final UserService userService;

  private final LogIn logIn;
  private final BankAccountService bankAccountService;
  private final ModelMapper modelMapper;

  @Autowired
  public UserController(UserService userService,
                        LogIn logIn, BankAccountService bankAccountService, ModelMapper modelMapper) {
    this.userService = userService;
    this.logIn = logIn;
    this.bankAccountService = bankAccountService;
    this.modelMapper = modelMapper;
  }

  @GetMapping("/users")
  public UsersListDto getListOfUsers() {
    List<User> entities = userService.findAll();

    List<UserWithoutAccountsDto> dtos = entities.stream()
        .map(e -> modelMapper.map(e, UserWithoutAccountsDto.class))
        .toList();

    return new UsersListDto(dtos);
  }

  @GetMapping("/users/{user-id}")
  public UserResponseDto getUserById(@PathVariable("user-id") Long id) {
    Optional<User> userOptional = userService.findUserById(id);

    if (userOptional.isEmpty()) {
      throw new NotFoundException(NotFoundException.USER_NOT_FOUND);
    }

    return mapAndFetchBankAccounts(userOptional.get());
  }

  @PostMapping("/login/{login}/{password}")
  public void loginUser(@Valid @RequestBody UserForm form) {
    User user = userService.findUserByLogin(form.getLogin());
    if (user.getPassword().equals(form.getPassword())) {
      throw new NotFoundException(NotFoundException.USER_NOT_FOUND);
    }
    logIn.setLoginedUser(user);
  }

  private UserResponseDto mapAndFetchBankAccounts(User user) {
    List<BankAccount> bankAccountEntities
        = userService.findBankAccountsOfUser(user);
    List<BankAccountsWithoutUserDto> bankAccountDtos = bankAccountEntities.stream()
        .map(e -> modelMapper.map(e, BankAccountsWithoutUserDto.class))
        .toList();

    UserResponseDto resultDto
        = modelMapper.map(user, UserResponseDto.class);
    resultDto.setBank_accounts(bankAccountDtos);

    return resultDto;
  }

}