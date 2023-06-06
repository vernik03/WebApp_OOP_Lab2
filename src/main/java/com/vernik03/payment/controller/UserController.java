package com.vernik03.payment.controller;

import com.vernik03.payment.controller.dto.IdToLinkUpDto;
import com.vernik03.payment.controller.dto.user.UserForm;
import com.vernik03.payment.controller.dto.user.UserResponseDto;
import com.vernik03.payment.controller.dto.user.UserWithoutAccountsDto;
import com.vernik03.payment.controller.dto.user.UsersListDto;
import com.vernik03.payment.controller.dto.bankaccount.BankAccountsWithoutUserDto;
import com.vernik03.payment.exception.NotFoundException;
import com.vernik03.payment.model.User;
import com.vernik03.payment.model.BankAccount;
import com.vernik03.payment.service.UserService;
import com.vernik03.payment.service.CreditCardService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

  private final UserService userService;
  private final CreditCardService linkService;
  private final ModelMapper modelMapper;

  @Autowired
  public UserController(UserService userService,
                        CreditCardService linkService,
                        ModelMapper modelMapper) {
    this.userService = userService;
    this.linkService = linkService;
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

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponseDto createUser(@Valid @RequestBody UserForm form) {
    User toSave = modelMapper.map(form, User.class);
    User response = userService.saveUser(toSave);

    return modelMapper.map(response, UserResponseDto.class);
  }

  @PutMapping("/users/{user-id}")
  public UserResponseDto updateUser(@PathVariable("user-id") Long id,
                                    @Valid @RequestBody UserForm form) {
    if (!userService.existsById(id)) {
      throw new NotFoundException(NotFoundException.USER_NOT_FOUND);
    }

    User toUpdate = modelMapper.map(form, User.class);
    toUpdate.setId(id);

    User response = userService.updateUser(toUpdate);

    return mapAndFetchBankAccounts(response);
  }

  @DeleteMapping("/users/{user-id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable("user-id") Long id) {
    boolean isDeleted = userService.deleteUserById(id);

    if (!isDeleted) {
      throw new NotFoundException(NotFoundException.USER_NOT_FOUND);
    }
  }

  @PostMapping("/users/{user-id}/bank-accounts")
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponseDto linkUpBankAccount(@PathVariable("user-id") Long userId,
                                           @Valid @RequestBody IdToLinkUpDto bankAccountId) {
    //existence validation is here
    linkService.linkUpUserAndBankAccount(userId, bankAccountId.getIdToLink());

    Optional<User> userOptional = userService.findUserById(userId);

    return mapAndFetchBankAccounts(userOptional.get());
  }

  @DeleteMapping("/users/{user-id}/bank-accounts/{bank-account-id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unlinkUpBankAccount(@PathVariable("user-id") Long userId,
                                  @PathVariable("bank-account-id") Long bankAccountId) {
    linkService.unlinkUpUserAndBankAccount(userId, bankAccountId);
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