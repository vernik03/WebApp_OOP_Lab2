package com.vernik03.paymentapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vernik03.paymentapp.model.enums.Position;
import com.vernik03.paymentapp.service.CrewMemberService;
import com.vernik03.paymentapp.controller.dto.IdToLinkUpDto;
import com.vernik03.paymentapp.controller.dto.crewmember.CrewMemberForm;
import com.vernik03.paymentapp.controller.dto.crewmember.CrewMemberResponseDto;
import com.vernik03.paymentapp.controller.dto.crewmember.CrewMemberWithoutFlightsDto;
import com.vernik03.paymentapp.controller.dto.crewmember.CrewMembersListDto;
import com.vernik03.paymentapp.exception.ErrorMessage;

import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@SqlGroup({
    @Sql(scripts = "/clear.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(scripts = "/test-init.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
})
class UserControllerTest {

  @Autowired
  private CrewMemberService crewMemberService;

  @Autowired
  private CrewMemberController crewMemberController;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void contextLoads() {
    assertNotNull(crewMemberService);
    assertNotNull(crewMemberController);
    assertNotNull(restTemplate);
  }

  @Test
  void getListOfCrewMembersWorksProperly() {
    ResponseEntity<CrewMembersListDto> response = restTemplate.getForEntity(
        "/crew-members",
        CrewMembersListDto.class
    );

    List<CrewMemberWithoutFlightsDto> crewMembers = response.getBody().getCrewMembers();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(4, crewMembers.size());
    assertTrue(crewMembers.stream().anyMatch(m -> Objects.equals("Viktor", m.getName())));
  }

  @Test
  void getCrewMemberByIdWhenProvidedNonExistentId() {
    ResponseEntity<ErrorMessage> response = restTemplate.getForEntity(
        "/crew-members/{crew-member-id}",
        ErrorMessage.class,
        0L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("crew_member_not_found", responseBody.getErrorId());
  }

  @Test
  void getCrewMemberByIdWhenProvidedExistentId() {
    ResponseEntity<CrewMemberResponseDto> response = restTemplate.getForEntity(
        "/crew-members/{crew-member-id}",
        CrewMemberResponseDto.class,
        1L
    );

    CrewMemberResponseDto crewMember = response.getBody();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1L, crewMember.getId());
    assertEquals("Viktor", crewMember.getName());
    assertEquals("Muzyka", crewMember.getSurname());
    Assertions.assertEquals(Position.NAVIGATOR, crewMember.getPosition());
    assertEquals(2, crewMember.getFlights().size());
  }

  @Test
  void createCrewMemberWorksProperly() {
    CrewMemberForm form = new CrewMemberForm(
        "Andrii",
        "Prokopenko",
        Position.NAVIGATOR
    );

    ResponseEntity<CrewMemberResponseDto> response = restTemplate.postForEntity(
        "/crew-members",
        form,
        CrewMemberResponseDto.class
    );

    CrewMemberResponseDto crewMember = response.getBody();

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(5L, crewMember.getId());
    assertEquals("Andrii", crewMember.getName());
    assertEquals("Prokopenko", crewMember.getSurname());
    Assertions.assertEquals(Position.NAVIGATOR, crewMember.getPosition());
  }

  @Test
  void updateCrewMemberWhenProvidedNonExistentId() {
    CrewMemberForm form = new CrewMemberForm(
        "Andrii",
        "Prokopenko",
        Position.NAVIGATOR
    );

    HttpEntity<CrewMemberForm> httpEntity = new HttpEntity<>(form);
    ResponseEntity<ErrorMessage> response = restTemplate.exchange(
        "/crew-members/{crew-member-id}",
        HttpMethod.PUT,
        httpEntity,
        ErrorMessage.class,
        0L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("crew_member_not_found", responseBody.getErrorId());
  }

  @Test
  void updateCrewMemberWhenProvidedExistentId() {
    CrewMemberForm form = new CrewMemberForm(
        "Andrii",
        "Prokopenko",
        Position.NAVIGATOR
    );

    HttpEntity<CrewMemberForm> httpEntity = new HttpEntity<>(form);
    ResponseEntity<CrewMemberResponseDto> response = restTemplate.exchange(
        "/crew-members/{crew-member-id}",
        HttpMethod.PUT,
        httpEntity,
        CrewMemberResponseDto.class,
        1L
    );

    CrewMemberResponseDto crewMember = response.getBody();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1L, crewMember.getId());
    assertEquals("Andrii", crewMember.getName());
    assertEquals("Prokopenko", crewMember.getSurname());
    Assertions.assertEquals(Position.NAVIGATOR, crewMember.getPosition());
  }

  @Test
  void deleteCrewMemberWhenProvidedNonExistentId() {
    ResponseEntity<ErrorMessage> response = restTemplate.exchange(
        "/crew-members/{crew-member-id}",
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        ErrorMessage.class,
        0L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("crew_member_not_found", responseBody.getErrorId());
  }

  @Test
  void deleteCrewMemberWhenProvidedExistentId() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "/crew-members/{crew-member-id}",
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        Void.class,
        1L
    );

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void linkUpFlightWhenProvidedNonExistentCrewMemberId() {
    IdToLinkUpDto flightId = new IdToLinkUpDto(2L);

    ResponseEntity<ErrorMessage> response = restTemplate.postForEntity(
        "/crew-members/{crew-member-id}/credit_cards",
        flightId,
        ErrorMessage.class,
        42L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("crew_member_not_found_to_link_up", responseBody.getErrorId());
  }

  @Test
  void linkUpFlightWhenProvidedNonExistentFlightId() {
    IdToLinkUpDto flightId = new IdToLinkUpDto(42L);

    ResponseEntity<ErrorMessage> response = restTemplate.postForEntity(
        "/crew-members/{crew-member-id}/credit_cards",
        flightId,
        ErrorMessage.class,
        1L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("flight_not_found_to_link_up", responseBody.getErrorId());
  }

  @Test
  void linkUpFlightWhenLinkAlreadyExists() {
    IdToLinkUpDto flightId = new IdToLinkUpDto(1L);

    ResponseEntity<ErrorMessage> response = restTemplate.postForEntity(
        "/crew-members/{crew-member-id}/credit_cards",
        flightId,
        ErrorMessage.class,
        1L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("link_already_exists", responseBody.getErrorId());
  }

  @Test
  void linkUpFlightWorksProperly() {
    IdToLinkUpDto flightId = new IdToLinkUpDto(2L);

    ResponseEntity<CrewMemberResponseDto> response = restTemplate.postForEntity(
        "/crew-members/{crew-member-id}/credit_cards",
        flightId,
        CrewMemberResponseDto.class,
        4L
    );

    CrewMemberResponseDto crewMember = response.getBody();

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(3, crewMember.getFlights().size());
  }

  @Test
  void unlinkUpFlightWhenProvidedNonExistentCrewMemberId() {
    ResponseEntity<ErrorMessage> response = restTemplate.exchange(
        "/crew-members/{crew-member-id}/credit_cards/{flight-id}",
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        ErrorMessage.class,
        42L,
        1L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("crew_member_not_found_to_unlink_up", responseBody.getErrorId());
  }

  @Test
  void unlinkUpFlightWhenProvidedNonExistentFlightId() {
    ResponseEntity<ErrorMessage> response = restTemplate.exchange(
        "/crew-members/{crew-member-id}/credit_cards/{flight-id}",
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        ErrorMessage.class,
        1L,
        42L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("flight_not_found_to_unlink_up", responseBody.getErrorId());
  }

  @Test
  void unlinkUpFlightWhenLinkIsAbsent() {
    ResponseEntity<ErrorMessage> response = restTemplate.exchange(
        "/crew-members/{crew-member-id}/credit_cards/{flight-id}",
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        ErrorMessage.class,
        4L,
        2L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("link_is_absent", responseBody.getErrorId());
  }

  @Test
  void unlinkUpFlightWorksProperly() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "/crew-members/{crew-member-id}/credit_cards/{flight-id}",
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        Void.class,
        4L,
        3L
    );

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

}