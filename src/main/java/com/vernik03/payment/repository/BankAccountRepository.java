package com.vernik03.payment.repository;

import com.vernik03.payment.model.User;
import com.vernik03.payment.model.BankAccount;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

  long deleteBankAccountById(Long id);

  List<BankAccount> findBankAccountsByUser(User user);

  Optional<BankAccount> findByCardNumber(String number);

}
