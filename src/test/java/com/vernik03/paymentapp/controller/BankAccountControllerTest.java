package com.vernik03.paymentapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vernik03.paymentapp.service.FlightService;
import com.vernik03.paymentapp.controller.dto.IdToLinkUpDto;
import com.vernik03.paymentapp.controller.dto.flight.FlightForm;
import com.vernik03.paymentapp.controller.dto.flight.FlightResponseDto;
import com.vernik03.paymentapp.controller.dto.flight.FlightWithoutCrewMembersDto;
import com.vernik03.paymentapp.controller.dto.flight.FlightsListDto;
import com.vernik03.paymentapp.exception.ErrorMessage;

import java.time.LocalDateTime;
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
class BankAccountControllerTest {

  @Autowired
  private FlightService flightService;

  @Autowired
  private FlightController flightController;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void contextLoads() {
    assertNotNull(flightService);
    assertNotNull(flightController);
    assertNotNull(restTemplate);
  }

  @Test
  void getListOfFlightsWorksProperly() {
    ResponseEntity<FlightsListDto> response = restTemplate.getForEntity(
        "/credit_cards",
        FlightsListDto.class
    );

    List<FlightWithoutCrewMembersDto> credit_cards = response.getBody().getFlights();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(3, credit_cards.size());
    assertTrue(credit_cards.stream().anyMatch(f -> Objects.equals("Krakow", f.getDestination())));
  }

  @Test
  void getFlightWhenProvidedNonExistentId() {
    ResponseEntity<ErrorMessage> response = restTemplate.getForEntity(
        "/credit_cards/{flight-id}",
        ErrorMessage.class,
        42L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("flight_not_found", responseBody.getErrorId());
  }

  @Test
  void getFlightWhenProvidedExistentId() {
    //2023-05-23 23:53:00 - expected departure time
    //2023-05-24 16:09:00 - expected arrival time
    LocalDateTime expectedDepartureTime = LocalDateTime.of(2023, 5, 23, 23, 53);
    LocalDateTime expectedArrivalTime = LocalDateTime.of(2023, 5, 24, 16, 9);

    ResponseEntity<FlightResponseDto> response = restTemplate.getForEntity(
        "/credit_cards/{flight-id}",
        FlightResponseDto.class,
        1L
    );

    FlightResponseDto flight = response.getBody();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1L, flight.getId());
    assertEquals("Kyiv", flight.getDepartureFrom());
    assertEquals("Krakow", flight.getDestination());
    assertEquals(expectedDepartureTime, flight.getDepartureTime());
    assertEquals(expectedArrivalTime, flight.getArrivalTime());
    assertEquals(3, flight.getCrewMembers().size());
  }

  @Test
  void createFlightWorksProperly() {
    LocalDateTime departureTime = LocalDateTime.now().plusHours(1L);
    LocalDateTime arrivalTime = LocalDateTime.now().plusHours(6L);

    FlightForm form = new FlightForm(
        "Kyiv",
        "Uzhhorod",
        departureTime,
        arrivalTime
    );

    ResponseEntity<FlightResponseDto> response = restTemplate.postForEntity(
        "/credit_cards",
        form,
        FlightResponseDto.class
    );

    FlightResponseDto flight = response.getBody();

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(4L, flight.getId());
    assertEquals("Kyiv", flight.getDepartureFrom());
    assertEquals("Uzhhorod", flight.getDestination());
    assertEquals(departureTime, flight.getDepartureTime());
    assertEquals(arrivalTime, flight.getArrivalTime());
  }

  @Test
  void updateFlightWhenProvidedNonExistentId() {
    LocalDateTime departureTime = LocalDateTime.now().plusHours(1L);
    LocalDateTime arrivalTime = LocalDateTime.now().plusHours(6L);

    FlightForm form = new FlightForm(
        "Zhytomyr",
        "Rivne",
        departureTime,
        arrivalTime
    );

    HttpEntity<FlightForm> httpEntity = new HttpEntity<>(form);
    ResponseEntity<ErrorMessage> response = restTemplate.exchange(
        "/credit_cards/{flight-id}",
        HttpMethod.PUT,
        httpEntity,
        ErrorMessage.class,
        42L
    );

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void updateFlightWhenProvidedExistentId() {
    LocalDateTime departureTime = LocalDateTime.now().plusHours(1L);
    LocalDateTime arrivalTime = LocalDateTime.now().plusHours(6L);

    FlightForm form = new FlightForm(
        "Zhytomyr",
        "Rivne",
        departureTime,
        arrivalTime
    );

    HttpEntity<FlightForm> httpEntity = new HttpEntity<>(form);
    ResponseEntity<FlightResponseDto> response = restTemplate.exchange(
        "/credit_cards/{flight-id}",
        HttpMethod.PUT,
        httpEntity,
        FlightResponseDto.class,
        1L
    );

    FlightResponseDto flight = response.getBody();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1L, flight.getId());
    assertEquals("Zhytomyr", flight.getDepartureFrom());
    assertEquals("Rivne", flight.getDestination());
    assertEquals(departureTime, flight.getDepartureTime());
    assertEquals(arrivalTime, flight.getArrivalTime());
  }

  @Test
  void deleteFlightWhenProvidedNonExistentId() {
    ResponseEntity<ErrorMessage> response = restTemplate.exchange(
        "/credit_cards/{flight-id}",
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        ErrorMessage.class,
        42L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("flight_not_found", responseBody.getErrorId());
  }

  @Test
  void deleteFlightWhenProvidedExistentId() {
    ResponseEntity<ErrorMessage> response = restTemplate.exchange(
        "/credit_cards/{flight-id}",
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        ErrorMessage.class,
        1L
    );

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void linkUpCrewMemberWhenProvidedNonExistentCrewMemberId() {
    IdToLinkUpDto crewMemberId = new IdToLinkUpDto(42L);

    ResponseEntity<ErrorMessage> response = restTemplate.postForEntity(
        "/credit_cards/{flight-id}/crew-members",
        crewMemberId,
        ErrorMessage.class,
        1L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("crew_member_not_found_to_link_up", responseBody.getErrorId());
  }

  @Test
  void linkUpCrewMemberWhenProvidedNonExistentFlightId() {
    IdToLinkUpDto crewMemberId = new IdToLinkUpDto(1L);

    ResponseEntity<ErrorMessage> response = restTemplate.postForEntity(
        "/credit_cards/{flight-id}/crew-members",
        crewMemberId,
        ErrorMessage.class,
        42L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("flight_not_found_to_link_up", responseBody.getErrorId());
  }

  @Test
  void linkUpCrewMemberWhenLinkAlreadyExists() {
    IdToLinkUpDto crewMemberId = new IdToLinkUpDto(1L);

    ResponseEntity<ErrorMessage> response = restTemplate.postForEntity(
        "/credit_cards/{flight-id}/crew-members",
        crewMemberId,
        ErrorMessage.class,
        3L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("link_already_exists", responseBody.getErrorId());
  }

  @Test
  void linkUpCrewMemberWorksProperly() {
    IdToLinkUpDto crewMemberId = new IdToLinkUpDto(1L);

    ResponseEntity<FlightResponseDto> response = restTemplate.postForEntity(
        "/credit_cards/{flight-id}/crew-members",
        crewMemberId,
        FlightResponseDto.class,
        2L
    );

    FlightResponseDto flight = response.getBody();

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(3, flight.getCrewMembers().size());
  }

  @Test
  void unlinkUpCrewMemberWhenProvidedNonExistentCrewMemberId() {
    ResponseEntity<ErrorMessage> response = restTemplate.exchange(
        "/credit_cards/{flight-id}/crew-members/{crew-member-id}",
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        ErrorMessage.class,
        1L,
        42L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("crew_member_not_found_to_unlink_up", responseBody.getErrorId());
  }

  @Test
  void unlinkUpCrewMemberWhenProvidedNonExistentFlightId() {
    ResponseEntity<ErrorMessage> response = restTemplate.exchange(
        "/credit_cards/{flight-id}/crew-members/{crew-member-id}",
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        ErrorMessage.class,
        42L,
        1L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("flight_not_found_to_unlink_up", responseBody.getErrorId());
  }

  @Test
  void unlinkUpCrewMemberWhenLinkIsAbsent() {
    ResponseEntity<ErrorMessage> response = restTemplate.exchange(
        "/credit_cards/{flight-id}/crew-members/{crew-member-id}",
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        ErrorMessage.class,
        1L,
        2L
    );

    ErrorMessage responseBody = response.getBody();

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("link_is_absent", responseBody.getErrorId());
  }

  @Test
  void unlinkUpCrewMemberWorksProperly() {
    ResponseEntity<ErrorMessage> response = restTemplate.exchange(
        "/credit_cards/{flight-id}/crew-members/{crew-member-id}",
        HttpMethod.DELETE,
        HttpEntity.EMPTY,
        ErrorMessage.class,
        1L,
        3L
    );

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

}