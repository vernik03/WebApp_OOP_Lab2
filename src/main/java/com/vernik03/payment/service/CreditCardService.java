package com.vernik03.payment.service;

import com.vernik03.payment.model.BankAccount;
import com.vernik03.payment.exception.NotFoundException;
import com.vernik03.payment.exception.ValidException;
import com.vernik03.payment.model.User;
import com.vernik03.payment.repository.UserRepository;
import com.vernik03.payment.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditCardService {

  private final UserRepository userRepository;
  private final BankAccountRepository bankAccountRepository;

  @Autowired
  public CreditCardService(UserRepository userRepository,
                           BankAccountRepository bankAccountRepository) {
    this.userRepository = userRepository;
    this.bankAccountRepository = bankAccountRepository;
  }

  @Transactional
  public void linkUpUserAndBankAccount(Long userId, Long bankAccountId) {
    User user = userRepository
        .findById(userId)
        .orElseThrow(
            () -> new NotFoundException(NotFoundException.USER_NOT_FOUND_TO_LINK_UP)
        );

    BankAccount bankAccount = bankAccountRepository
        .findById(bankAccountId)
        .orElseThrow(
            () -> new NotFoundException(NotFoundException.BANK_ACCOUNT_NOT_FOUND_TO_LINK_UP)
        );

    boolean linkExists = checkLinkExistence(user, bankAccount);

    if (linkExists) {
      throw new ValidException(ValidException.LINK_ALREADY_EXISTS);
    }

    bankAccount.getUsers().add(user);
    user.getBank_accounts().add(bankAccount);
  }

  @Transactional
  public void unlinkUpUserAndCreditCard(Long userId, Long bankAccountId) {
    User user = userRepository
        .findById(userId)
        .orElseThrow(
            () -> new NotFoundException(NotFoundException.USER_NOT_FOUND_TO_UNLINK_UP)
        );

    BankAccount bankAccount = bankAccountRepository
        .findById(bankAccountId)
        .orElseThrow(
            () -> new NotFoundException(NotFoundException.BANK_ACCOUNT_NOT_FOUND_TO_UNLINK_UP)
        );

    bankAccount.getUsers().remove(user);
    boolean isUnlinked = user.getBank_accounts().remove(bankAccount);

    if (!isUnlinked) {
      throw new NotFoundException(NotFoundException.LINK_IS_ABSENT);
    }
  }

  private boolean checkLinkExistence(User user, BankAccount bankAccount) {
    return user.getBank_accounts().contains(bankAccount);
  }

}
