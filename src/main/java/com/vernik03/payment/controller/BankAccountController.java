package com.vernik03.payment.controller;

import com.vernik03.payment.controller.dto.IdToLinkUpDto;
import com.vernik03.payment.controller.dto.user.UserWithoutAccountsDto;
import com.vernik03.payment.model.User;
import com.vernik03.payment.model.BankAccount;
import com.vernik03.payment.service.CreditCardService;
import com.vernik03.payment.service.BankAccountService;
import com.vernik03.payment.controller.dto.bankaccount.BankAccountForm;
import com.vernik03.payment.controller.dto.bankaccount.BankAccountResponseDto;
import com.vernik03.payment.controller.dto.bankaccount.BankAccountsWithoutUserDto;
import com.vernik03.payment.controller.dto.bankaccount.BankAccountsListDto;
import com.vernik03.payment.exception.NotFoundException;
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
public class BankAccountController {

  private final BankAccountService bankAccountService;
  private final CreditCardService linkService;
  private final ModelMapper modelMapper;

  @Autowired
  public BankAccountController(BankAccountService bankAccountService,
                               CreditCardService linkService,
                               ModelMapper modelMapper) {
    this.bankAccountService = bankAccountService;
    this.linkService = linkService;
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

  @PostMapping("/bank-accounts")
  @ResponseStatus(HttpStatus.CREATED)
  public BankAccountResponseDto createBankAccount(@Valid @RequestBody BankAccountForm form) {
    BankAccount toSave = modelMapper.map(form, BankAccount.class);
    BankAccount response = bankAccountService.saveBankAccount(toSave);

    return modelMapper.map(response, BankAccountResponseDto.class);
  }

  @PutMapping("/bank-accounts/{bank-account-id}")
  public BankAccountResponseDto updateBankAccount(@PathVariable("bank-account-id") Long id,
                                                  @Valid @RequestBody BankAccountForm form) {
    if (!bankAccountService.existsById(id)) {
      throw new NotFoundException(NotFoundException.BANK_ACCOUNT_NOT_FOUND);
    }

    BankAccount toUpdate = modelMapper.map(form, BankAccount.class);
    toUpdate.setId(id);

    BankAccount response = bankAccountService.updateBankAccount(toUpdate);

    return mapAndFetchUsers(response);
  }

  @DeleteMapping("/bank-accounts/{bank-account-id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteBankAccount(@PathVariable("bank-account-id") Long id) {
    boolean isDeleted = bankAccountService.deleteBankAccountById(id);

    if (!isDeleted) {
      throw new NotFoundException(NotFoundException.BANK_ACCOUNT_NOT_FOUND);
    }
  }

  @PostMapping("/bank-accounts/{bank-account-id}/users")
  @ResponseStatus(HttpStatus.CREATED)
  public BankAccountResponseDto linkUpUser(@PathVariable("bank-account-id") Long bankAccountId,
                                           @Valid @RequestBody IdToLinkUpDto userId) {

    linkService.linkUpUserAndBankAccount(userId.getIdToLink(), bankAccountId);

    Optional<BankAccount> bankAccountOptional = bankAccountService.findBankAccountById(bankAccountId);

    return mapAndFetchUsers(bankAccountOptional.get());
  }

  @DeleteMapping("/bank-accounts/{bank-account-id}/users/{user-id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unlinkUpUser(@PathVariable("bank-account-id") Long bankAccountId,
                           @PathVariable("user-id") Long userId) {
    linkService.unlinkUpUserAndBankAccount(userId, bankAccountId);
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