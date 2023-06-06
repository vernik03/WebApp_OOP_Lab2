package com.vernik03.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vernik03.payment.model.User;
import com.vernik03.payment.model.BankAccount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
class BankAccountServiceTest {

  @Autowired
  private BankAccountService bankAccountService;

  @Test
  void saveFlightWorksProperly() {
    LocalDateTime departureTime = LocalDateTime.now().plusHours(1L);
    LocalDateTime arrivalTime = LocalDateTime.now().plusHours(6L);

    BankAccount toSave = BankAccount.builder()
        .departureFrom("Kyiv")
        .destination("Zhytomyr")
        .departureTime(departureTime)
        .arrivalTime(arrivalTime)
        .build();

    BankAccount actual = bankAccountService.saveBankAccount(toSave);

    assertEquals(4L, actual.getId());
    assertEquals("Kyiv", actual.getDepartureFrom());
    assertEquals("Zhytomyr", actual.getDestination());
    assertEquals(departureTime, actual.getDepartureTime());
    assertEquals(arrivalTime, actual.getArrivalTime());
  }

  @Test
  void findFlightByIdWhenProvidedExistentId() {
    //2023-05-23 23:53:00 - expected departure time
    //2023-05-24 16:09:00 - expected arrival time
    LocalDateTime expectedDepartureTime = LocalDateTime.of(2023, 5, 23, 23, 53);
    LocalDateTime expectedArrivalTime = LocalDateTime.of(2023, 5, 24, 16, 9);

    Optional<BankAccount> actual = bankAccountService.findBankAccountById(1L);

    assertTrue(actual.isPresent());
    assertEquals("Kyiv", actual.get().getDepartureFrom());
    assertEquals("Krakow", actual.get().getDestination());
    assertEquals(expectedDepartureTime, actual.get().getDepartureTime());
    assertEquals(expectedArrivalTime, actual.get().getArrivalTime());
  }

  @Test
  void findFlightByIdWhenProvidedNonExistentId() {
    Optional<BankAccount> actual = bankAccountService.findBankAccountById(0L);

    assertTrue(actual.isEmpty());
  }

  @Test
  void existsByIdWhenProvidedExistentId() {
    boolean isFound = bankAccountService.existsById(1L);

    assertTrue(isFound);
  }

  @Test
  void existsByIdWhenProvidedNonExistentId() {
    boolean isFound = bankAccountService.existsById(0L);

    assertFalse(isFound);
  }

  @Test
  void findAllWorksProperly() {
    List<BankAccount> credit_cards = bankAccountService.findAll();

    assertEquals(3, credit_cards.size());
    assertTrue(credit_cards.stream().anyMatch(f -> Objects.equals("Krakow", f.getDestination())));
  }

  @Test
  void findCrewMembersOfFlightWorksProperly() {
    Optional<BankAccount> flightOptional = bankAccountService.findBankAccountById(1L);
    BankAccount bankAccount = flightOptional.orElseThrow();

    List<User> users = bankAccountService.findUserOfBankAccount(bankAccount);

    assertEquals(3, users.size());
    assertTrue(users.stream().anyMatch(m -> Objects.equals("Prokopchuk", m.getSurname())));
  }

  @Test
  void updateFlightWorksProperly() {
    LocalDateTime departureTime = LocalDateTime.now().plusHours(1L);
    LocalDateTime arrivalTime = LocalDateTime.now().plusHours(6L);

    BankAccount toUpdate = BankAccount.builder()
        .id(1L)
        .departureFrom("Lviv")
        .destination("Zhytomyr")
        .departureTime(departureTime)
        .arrivalTime(arrivalTime)
        .build();

    BankAccount actual = bankAccountService.updateBankAccount(toUpdate);

    assertEquals(1L, actual.getId());
    assertEquals("Lviv", actual.getDepartureFrom());
    assertEquals("Zhytomyr", actual.getDestination());
    assertEquals(departureTime, actual.getDepartureTime());
    assertEquals(arrivalTime, actual.getArrivalTime());
  }

  @Test
  void deleteFlightByIdWhenProvidedExistentId() {
    boolean isDeleted = bankAccountService.deleteBankAccountById(1L);

    assertTrue(isDeleted);
  }

  @Test
  void deleteFlightByIdWhenProvidedNonExistentId() {
    boolean isDeleted = bankAccountService.deleteBankAccountById(0L);

    assertFalse(isDeleted);
  }

}