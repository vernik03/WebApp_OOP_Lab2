package com.vernik03.paymentapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.vernik03.paymentapp.model.CrewMember;
import com.vernik03.paymentapp.model.Flight;
import com.vernik03.paymentapp.model.enums.Position;
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
  private CrewMemberService crewMemberService;

  @Test
  void contextLoads() {
    assertNotNull(crewMemberService);
  }

  @Test
  void saveCrewMemberWorksProperly() {
    CrewMember toSave = CrewMember.builder()
        .name("Andrii")
        .surname("Petrenko")
        .position(Position.NAVIGATOR)
        .build();

    CrewMember actual = crewMemberService.saveCrewMember(toSave);

    assertEquals(5L, actual.getId());
    assertEquals("Andrii", actual.getName());
    assertEquals("Petrenko", actual.getSurname());
    assertEquals(Position.NAVIGATOR, actual.getPosition());
  }

  @Test
  void findCrewMemberByIdWhenProvidedExistentId() {
    Optional<CrewMember> actualOptional = crewMemberService.findCrewMemberById(1L);
    CrewMember actual = actualOptional.orElseThrow();

    assertEquals("Viktor", actual.getName());
    assertEquals("Muzyka", actual.getSurname());
    assertEquals(Position.NAVIGATOR, actual.getPosition());
  }

  @Test
  void findCrewMemberByIdWhenProvidedNonExistentId() {
    Optional<CrewMember> actual = crewMemberService.findCrewMemberById(0L);

    assertTrue(actual.isEmpty());
  }

  @Test
  void existsByIdWhenProvidedExistentId() {
    boolean isFound = crewMemberService.existsById(1L);

    assertTrue(isFound);
  }

  @Test
  void existsByIdWhenProvidedNonExistentId() {
    boolean isFound = crewMemberService.existsById(0L);

    assertFalse(isFound);
  }

  @Test
  void findAllWorksProperly() {
    List<CrewMember> crewMembers = crewMemberService.findAll();

    assertEquals(4, crewMembers.size());
    assertTrue(crewMembers.stream().anyMatch(m -> Objects.equals("Roman", m.getName())));
  }

  @Test
  void findFlightsOfCrewMemberWorksProperly() {
    Optional<CrewMember> crewMemberOptional = crewMemberService.findCrewMemberById(1L);
    CrewMember crewMember = crewMemberOptional.orElseThrow();

    List<Flight> credit_cards = crewMemberService.findFlightsOfCrewMember(crewMember);

    assertEquals(2, credit_cards.size());
    assertTrue(credit_cards.stream().anyMatch(f -> Objects.equals("Krakow", f.getDestination())));
  }

  @Test
  void updateCrewMemberWorksProperly() {
    CrewMember toUpdate = CrewMember.builder()
        .id(1L)
        .name("Ruslan")
        .surname("Prokopenko")
        .position(Position.OPERATOR)
        .build();

    CrewMember actual = crewMemberService.updateCrewMember(toUpdate);

    assertEquals(1L, actual.getId());
    assertEquals("Ruslan", actual.getName());
    assertEquals("Prokopenko", actual.getSurname());
    assertEquals(Position.OPERATOR, actual.getPosition());
  }

  @Test
  void deleteCrewMemberByIdWhenProvidedExistentId() {
    boolean isDeleted = crewMemberService.deleteCrewMemberById(1L);

    assertTrue(isDeleted);
  }

  @Test
  void deleteCrewMemberByIdWhenProvidedNonExistentId() {
    boolean isDeleted = crewMemberService.deleteCrewMemberById(0L);

    assertFalse(isDeleted);
  }

}