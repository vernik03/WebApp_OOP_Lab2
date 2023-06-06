package com.vernik03.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.vernik03.payment.model.User;
import com.vernik03.payment.model.BankAccount;
import com.vernik03.payment.model.enums.Position;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;

@SpringBootTest
@AutoConfigureTestDatabase
@SqlGroup({
    @Sql(scripts = "/clear.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(scripts = "/test-init.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
})
class UserServiceTest {

  @Autowired
  private UserService userService;

  @Test
  void contextLoads() {
    assertNotNull(userService);
  }

  @Test
  void saveCrewMemberWorksProperly() {
    User toSave = User.builder()
        .name("Andrii")
        .surname("Petrenko")
        .position(Position.NAVIGATOR)
        .build();

    User actual = userService.saveUser(toSave);

    assertEquals(5L, actual.getId());
    assertEquals("Andrii", actual.getName());
    assertEquals("Petrenko", actual.getSurname());
    assertEquals(Position.NAVIGATOR, actual.getPosition());
  }

  @Test
  void findCrewMemberByIdWhenProvidedExistentId() {
    Optional<User> actualOptional = userService.findUserById(1L);
    User actual = actualOptional.orElseThrow();

    assertEquals("Viktor", actual.getName());
    assertEquals("Muzyka", actual.getSurname());
    assertEquals(Position.NAVIGATOR, actual.getPosition());
  }

  @Test
  void findCrewMemberByIdWhenProvidedNonExistentId() {
    Optional<User> actual = userService.findUserById(0L);

    assertTrue(actual.isEmpty());
  }

  @Test
  void existsByIdWhenProvidedExistentId() {
    boolean isFound = userService.existsById(1L);

    assertTrue(isFound);
  }

  @Test
  void existsByIdWhenProvidedNonExistentId() {
    boolean isFound = userService.existsById(0L);

    assertFalse(isFound);
  }

  @Test
  void findAllWorksProperly() {
    List<User> users = userService.findAll();

    assertEquals(4, users.size());
    assertTrue(users.stream().anyMatch(m -> Objects.equals("Roman", m.getName())));
  }

  @Test
  void findFlightsOfCrewMemberWorksProperly() {
    Optional<User> crewMemberOptional = userService.findUserById(1L);
    User user = crewMemberOptional.orElseThrow();

    List<BankAccount> credit_cards = userService.findBankAccountsOfUser(user);

    assertEquals(2, credit_cards.size());
    assertTrue(credit_cards.stream().anyMatch(f -> Objects.equals("Krakow", f.getDestination())));
  }

  @Test
  void updateCrewMemberWorksProperly() {
    User toUpdate = User.builder()
        .id(1L)
        .name("Ruslan")
        .surname("Prokopenko")
        .position(Position.OPERATOR)
        .build();

    User actual = userService.updateUser(toUpdate);

    assertEquals(1L, actual.getId());
    assertEquals("Ruslan", actual.getName());
    assertEquals("Prokopenko", actual.getSurname());
    assertEquals(Position.OPERATOR, actual.getPosition());
  }

  @Test
  void deleteCrewMemberByIdWhenProvidedExistentId() {
    boolean isDeleted = userService.deleteUserById(1L);

    assertTrue(isDeleted);
  }

  @Test
  void deleteCrewMemberByIdWhenProvidedNonExistentId() {
    boolean isDeleted = userService.deleteUserById(0L);

    assertFalse(isDeleted);
  }

}