package com.vernik03.paymentapp.service;

import static org.junit.jupiter.api.Assertions.*;

import com.vernik03.paymentapp.model.CrewMember;
import com.vernik03.paymentapp.model.Flight;
import com.vernik03.paymentapp.exception.NotFoundException;
import com.vernik03.paymentapp.exception.ValidException;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@SqlGroup({
    @Sql(scripts = "/clear.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(scripts = "/test-init.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
})
class CreditCardServiceTest {

  @Autowired
  private CrewMemberService crewMemberService;

  @Autowired
  private CrewMembersFlightsLinkService linkService;

  @Test
  void linkUpCrewMemberAndFlightWhenProvidedNonExistentCrewMemberId() {
    Exception e = assertThrows(NotFoundException.class,
        () -> linkService.linkUpCrewMemberAndFlight(0L, 1L));

    assertEquals("Unable to link up due crew member absence" , e.getMessage());
  }

  @Test
  void linkUpCrewMemberAndFlightWhenProvidedNonExistentFlightId() {
    Exception e = assertThrows(NotFoundException.class,
        () -> linkService.linkUpCrewMemberAndFlight(2L, 42L));

    assertEquals("Unable to link up due flight absence" , e.getMessage());
  }

  @Test
  void linkUpCrewMemberAndFlightWhenLinkAlreadyExists() {
    Exception e = assertThrows(ValidException.class,
        () -> linkService.linkUpCrewMemberAndFlight(3L, 1L));

    assertEquals("Crew member and flight already linked up" , e.getMessage());
  }

  @Test
  void linkUpCrewMemberAndFlightWorksProperly() {
    linkService.linkUpCrewMemberAndFlight(1L, 2L);

    CrewMember crewMember = crewMemberService.findCrewMemberById(1L).orElseThrow();
    List<Flight> credit_cards = crewMemberService.findFlightsOfCrewMember(crewMember);

    assertEquals(3, credit_cards.size());
    assertTrue(credit_cards.stream().anyMatch(f -> Objects.equals(2L, f.getId())));
  }

  @Test
  void unlinkUpCrewMemberAndFlightWhenProvidedNonExistentCrewMemberId() {
    Exception e = assertThrows(NotFoundException.class,
        () -> linkService.unlinkUpCrewMemberAndFlight(42L, 1L));

    assertEquals("Unable to unlink up due crew member absence" , e.getMessage());
  }

  @Test
  void unlinkUpCrewMemberAndFlightWhenProvidedNonExistentFlightId() {
    Exception e = assertThrows(NotFoundException.class,
        () -> linkService.unlinkUpCrewMemberAndFlight(1L, 42L));

    assertEquals("Unable to unlink up due flight absence", e.getMessage());
  }

  @Test
  void unlinkUpCrewMemberAndFlightWhenLinkIsAbsent() {
    Exception e = assertThrows(NotFoundException.class,
        () -> linkService.unlinkUpCrewMemberAndFlight(4L, 2L));

    assertEquals("The link between these crew member and flight is absent", e.getMessage());
  }

  @Test
  void unlinkUpCrewMemberAndFlightWorksProperly() {
    linkService.unlinkUpCrewMemberAndFlight(1L, 1L);

    CrewMember crewMember = crewMemberService.findCrewMemberById(1L).orElseThrow();
    List<Flight> credit_cards = crewMemberService.findFlightsOfCrewMember(crewMember);

    assertEquals(1, credit_cards.size());
    assertFalse(credit_cards.stream().anyMatch(f -> Objects.equals(1L, f.getId())));
  }

}