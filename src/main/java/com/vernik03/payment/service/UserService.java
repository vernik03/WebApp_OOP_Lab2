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
public class UserService {

  private final UserRepository userRepository;
  private final BankAccountRepository bankAccountRepository;

  @Autowired
  public UserService(UserRepository userRepository,
                     BankAccountRepository bankAccountRepository) {
    this.userRepository = userRepository;
    this.bankAccountRepository = bankAccountRepository;
  }

  @Transactional
  public User saveUser(User user) {
    return userRepository.save(user);
  }

  public Optional<User> findUserById(Long id) {
    return userRepository.findById(id);
  }

  public boolean existsById(Long id) {
    return userRepository.existsById(id);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public List<BankAccount> findBankAccountsOfUser(User user) {
    return bankAccountRepository.findBankAccountsByUser(user);
  }

  @Transactional
  public User updateUser(User toUpdate) {
    return userRepository.save(toUpdate);
  }

  @Transactional
  public boolean deleteUserById(Long id) {
    return userRepository.deleteUserById(id) > 0L;
  }

  public User findUserByLogin(String login) {
    return userRepository.findUserByLogin(login);
  }
}
