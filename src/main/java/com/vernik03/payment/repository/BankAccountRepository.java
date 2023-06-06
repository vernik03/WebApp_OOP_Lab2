package com.vernik03.payment.repository;

import com.vernik03.payment.model.User;
import com.vernik03.payment.model.BankAccount;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

  long deleteBankAccountById(Long id);

  List<BankAccount> findBankAccountsByUsers(User user);

}
