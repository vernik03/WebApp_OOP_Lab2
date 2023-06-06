package com.vernik03.payment.repository;

import com.vernik03.payment.model.User;
import com.vernik03.payment.model.BankAccount;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  long deleteUserById(Long id);

  List<User> findUserByBankAccount(BankAccount bankAccount);

}
