package com.vernik03.payment.controller;

import com.vernik03.payment.controller.dto.user.UserWithoutAccountsDto;
import com.vernik03.payment.model.User;
import com.vernik03.payment.model.BankAccount;
import com.vernik03.payment.service.BankAccountService;
import com.vernik03.payment.controller.dto.bankaccount.BankAccountResponseDto;
import com.vernik03.payment.controller.dto.bankaccount.BankAccountsWithoutUserDto;
import com.vernik03.payment.controller.dto.bankaccount.BankAccountsListDto;
import com.vernik03.payment.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import com.vernik03.payment.security.LogIn;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BankAccountController {

  private final BankAccountService bankAccountService;

  private final LogIn logIn;
  private final ModelMapper modelMapper;

  @Autowired
  public BankAccountController(BankAccountService bankAccountService,
                               LogIn logIn, ModelMapper modelMapper) {
    this.bankAccountService = bankAccountService;
    this.logIn = logIn;
    this.modelMapper = modelMapper;
  }

  @GetMapping("/bank-accounts")
  public BankAccountsListDto getListOfBankAccounts() {
    List<BankAccount> entities = bankAccountService.findAll();

    List<BankAccountsWithoutUserDto> dtos = entities.stream()
        .map(e -> modelMapper.map(e, BankAccountsWithoutUserDto.class))
        .toList();

    return new BankAccountsListDto(dtos);
  }

  @GetMapping("/bank-accounts/{bank-account-id}")
  public BankAccountResponseDto getBankAccountById(@PathVariable("bank-account-id") Long id) {
    Optional<BankAccount> bankAccountOptional = bankAccountService.findBankAccountById(id);

    if (bankAccountOptional.isEmpty()) {
      throw new NotFoundException(NotFoundException.BANK_ACCOUNT_NOT_FOUND);
    }

    return mapAndFetchUsers(bankAccountOptional.get());
  }

  @PostMapping("/bank-accounts/{bank-account-id}/save-money/{amount}")
  public BankAccountResponseDto saveMoney(@PathVariable("bank-account-id") Long id,
                                          @PathVariable("amount") Double amount) {
      Optional<BankAccount> bankAccountOptional = bankAccountService.findBankAccountById(id);

      checkLogIn();
      checkBankAccountOwner(bankAccountOptional.get());

      if (bankAccountOptional.get().getIs_blocked()) {
        throw new IllegalArgumentException("Bank account is blocked");
      }
      if (bankAccountOptional.isEmpty()) {
      throw new NotFoundException(NotFoundException.BANK_ACCOUNT_NOT_FOUND);
      }

      BankAccount bankAccount = bankAccountOptional.get();
      if (bankAccount.getBalance() + amount < 0) {
      throw new IllegalArgumentException("Not enough money");
      }
      bankAccount.setBalance(bankAccount.getBalance() + amount);
      BankAccount response = bankAccountService.updateBankAccount(bankAccount);

      return mapAndFetchUsers(response);
  }

  @PostMapping("/bank-accounts/{bank-account-id}/send-money/{bank-account-id2}/{amount}")
  public BankAccountResponseDto sendMoney(@PathVariable("bank-account-id") Long id,
                                          @PathVariable("bank-account-id2") Long id2,
                                          @PathVariable("amount") Double amount) {
    Optional<BankAccount> bankAccountOptional = bankAccountService.findBankAccountById(id);
    Optional<BankAccount> bankAccountOptional2 = bankAccountService.findBankAccountById(id2);

    checkLogIn();
    checkBankAccountOwner(bankAccountOptional.get());

    if (bankAccountOptional.get().getIs_blocked()) {
      throw new IllegalArgumentException("Bank account source is blocked");
    }
    if (bankAccountOptional2.get().getIs_blocked()) {
      throw new IllegalArgumentException("Bank account target is blocked");
    }
    if (bankAccountOptional.isEmpty()) {
      throw new NotFoundException(NotFoundException.BANK_ACCOUNT_NOT_FOUND);
    }

    BankAccount bankAccount = bankAccountOptional.get();
    BankAccount bankAccount2 = bankAccountOptional2.get();
    if (bankAccount.getBalance() - amount < 0) {
      throw new IllegalArgumentException("Not enough money");
    }
    if(amount < 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }

    bankAccount2.setBalance(bankAccount2.getBalance() + amount);
    bankAccount.setBalance(bankAccount.getBalance() - amount);
    BankAccount response = bankAccountService.updateBankAccount(bankAccount2);

    return mapAndFetchUsers(response);
  }

  @PostMapping("/bank-accounts/{bank-account-id}/block")
  public void blockAccount(@PathVariable("bank-account-id") Long id) {
    BankAccount bankAccount = bankAccountService.findBankAccountById(id).get();

    checkLogIn();
    checkBankAccountOwner(bankAccount);

    bankAccountService.blockBankAccount(bankAccount.getId());
  }

  @PostMapping("/bank-accounts/{bank-account-id}/unblock")
  public void unblockAccount(@PathVariable("bank-account-id") Long id) {
      BankAccount bankAccount = bankAccountService.findBankAccountById(id).get();

      checkLogIn();
      checkAdmin();

      bankAccountService.unblockBankAccount(bankAccount.getId());
  }

  private void checkLogIn(){
    if (logIn.getLoginedUser() == null) {
      throw new IllegalArgumentException("You are not logged in");
    }
  }

  private void checkBankAccountOwner(BankAccount bankAccount) {
    if (bankAccount.getUser().getId().equals(logIn.getLoginedUser().getId())) {
      throw new IllegalArgumentException("You are not owner of this bank account");
    }
  }

  private void checkAdmin(){
    if (!logIn.getLoginedUser().getIs_admin()) {
      throw new IllegalArgumentException("You are not admin");
    }
  }


  private BankAccountResponseDto mapAndFetchUsers(BankAccount bankAccount) {
    List<User> userEntities = bankAccountService.findUserOfBankAccount(bankAccount);
    List<UserWithoutAccountsDto> usersDtos = userEntities.stream()
        .map(e -> modelMapper.map(e, UserWithoutAccountsDto.class))
        .toList();

    BankAccountResponseDto resultDto = modelMapper.map(bankAccount, BankAccountResponseDto.class);
    resultDto.setUsers(usersDtos);

    return resultDto;
  }

}