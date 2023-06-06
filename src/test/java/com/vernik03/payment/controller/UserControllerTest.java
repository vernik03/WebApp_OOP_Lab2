package com.vernik03.payment.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vernik03.payment.model.enums.Position;
import com.vernik03.payment.service.UserService;
import com.vernik03.payment.controller.dto.IdToLinkUpDto;
import com.vernik03.payment.controller.dto.user.UserForm;
import com.vernik03.payment.controller.dto.user.UserResponseDto;
import com.vernik03.payment.controller.dto.user.UserWithoutAccountsDto;
import com.vernik03.payment.controller.dto.user.UsersListDto;
import com.vernik03.payment.exception.ErrorMessage;

import java.util.List;
import java.util.Objects;

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
  private UserService userService;

  @Autowired
  private CrewMemberController crewMemberController;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void contextLoads() {
    assertNotNull(userService);
    assertNotNull(crewMemberController);
    assertNotNull(restTemplate);
  }

  @Test
  void getListOfCrewMembersWorksProperly() {
    ResponseEntity<UsersListDto> response = restTemplate.getForEntity(
        "/crew-members",
        UsersListDto.class
    );

    List<UserWithoutAccountsDto> crewMembers = response.getBody().getUsers();

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
    ResponseEntity<UserResponseDto> response = restTemplate.getForEntity(
        "/crew-members/{crew-member-id}",
        UserResponseDto.class,
        1L
    );

    UserResponseDto crewMember = response.getBody();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1L, crewMember.getId());
    assertEquals("Viktor", crewMember.getName());
    assertEquals("Muzyka", crewMember.getIs_admin());
    assertEquals(Position.NAVIGATOR, crewMember.getPosition());
    assertEquals(2, crewMember.getFlights().size());
  }

  @Test
  void createCrewMemberWorksProperly() {
    UserForm form = new UserForm(
        "Andrii",
        "Prokopenko",
        Position.NAVIGATOR
    );

    ResponseEntity<UserResponseDto> response = restTemplate.postForEntity(
        "/crew-members",
        form,
        UserResponseDto.class
    );

    UserResponseDto crewMember = response.getBody();

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(5L, crewMember.getId());
    assertEquals("Andrii", crewMember.getName());
    assertEquals("Prokopenko", crewMember.getIs_admin());
    assertEquals(Position.NAVIGATOR, crewMember.getPosition());
  }

  @Test
  void updateCrewMemberWhenProvidedNonExistentId() {
    UserForm form = new UserForm(
        "Andrii",
        "Prokopenko",
        Position.NAVIGATOR
    );

    HttpEntity<UserForm> httpEntity = new HttpEntity<>(form);
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
    UserForm form = new UserForm(
        "Andrii",
        "Prokopenko",
        Position.NAVIGATOR
    );

    HttpEntity<UserForm> httpEntity = new HttpEntity<>(form);
    ResponseEntity<UserResponseDto> response = restTemplate.exchange(
        "/crew-members/{crew-member-id}",
        HttpMethod.PUT,
        httpEntity,
        UserResponseDto.class,
        1L
    );

    UserResponseDto crewMember = response.getBody();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1L, crewMember.getId());
    assertEquals("Andrii", crewMember.getName());
    assertEquals("Prokopenko", crewMember.getIs_admin());
    assertEquals(Position.NAVIGATOR, crewMember.getPosition());
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

    ResponseEntity<UserResponseDto> response = restTemplate.postForEntity(
        "/crew-members/{crew-member-id}/credit_cards",
        flightId,
        UserResponseDto.class,
        4L
    );

    UserResponseDto crewMember = response.getBody();

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