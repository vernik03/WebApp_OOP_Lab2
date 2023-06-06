package com.vernik03.payment.service;

import com.vernik03.payment.model.BankAccount;
import com.vernik03.payment.model.User;
import com.vernik03.payment.repository.UserRepository;
import com.vernik03.payment.repository.BankAccountRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankAccountService {

  private final BankAccountRepository bankAccountRepository;
  private final UserRepository userRepository;

  @Autowired
  public BankAccountService(BankAccountRepository bankAccountRepository,
                            UserRepository userRepository) {
    this.bankAccountRepository = bankAccountRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public BankAccount saveBankAccount(BankAccount bankAccount) {
    return bankAccountRepository.save(bankAccount);
  }

  public Optional<BankAccount> findBankAccountById(Long id) {
    return bankAccountRepository.findById(id);
  }

  public boolean existsById(Long id) {
    return bankAccountRepository.existsById(id);
  }

  public List<BankAccount> findAll() {
    return bankAccountRepository.findAll();
  }

  public List<User> findUserOfBankAccount(BankAccount bankAccount) {
    return userRepository.findUserByBankAccount(bankAccount);
  }

  @Transactional
  public BankAccount updateBankAccount(BankAccount bankAccount) {
    return bankAccountRepository.save(bankAccount);
  }

  @Transactional
  public boolean deleteBankAccountById(Long id) {
    return bankAccountRepository.deleteBankAccountById(id) > 0L;
  }

}
