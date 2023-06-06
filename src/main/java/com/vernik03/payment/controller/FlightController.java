package com.vernik03.payment.controller;

import com.vernik03.payment.controller.dto.IdToLinkUpDto;
import com.vernik03.payment.controller.dto.user.UserWithoutAccountsDto;
import com.vernik03.payment.model.User;
import com.vernik03.payment.model.BankAccount;
import com.vernik03.payment.service.CreditCardService;
import com.vernik03.payment.service.BankAccountService;
import com.vernik03.payment.controller.dto.bankaccount.BankAccountForm;
import com.vernik03.payment.controller.dto.bankaccount.BankAccountResponseDto;
import com.vernik03.payment.controller.dto.bankaccount.BankAccountsWithoutUserDto;
import com.vernik03.payment.controller.dto.bankaccount.BankAccountsListDto;
import com.vernik03.payment.exception.NotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlightController {

  private final BankAccountService bankAccountService;
  private final CreditCardService linkService;
  private final ModelMapper modelMapper;

  @Autowired
  public FlightController(BankAccountService bankAccountService,
                          CreditCardService linkService,
                          ModelMapper modelMapper) {
    this.bankAccountService = bankAccountService;
    this.linkService = linkService;
    this.modelMapper = modelMapper;
  }

  @GetMapping("/credit_cards")
  public BankAccountsListDto getListOfFlights() {
    List<BankAccount> entities = bankAccountService.findAll();

    List<BankAccountsWithoutUserDto> dtos = entities.stream()
        .map(e -> modelMapper.map(e, BankAccountsWithoutUserDto.class))
        .toList();

    return new BankAccountsListDto(dtos);
  }

  @GetMapping("/credit_cards/{flight-id}")
  public BankAccountResponseDto getFlightById(@PathVariable("flight-id") Long id) {
    Optional<BankAccount> flightOptional = bankAccountService.findBankAccountById(id);

    if (flightOptional.isEmpty()) {
      throw new NotFoundException(NotFoundException.FLIGHT_NOT_FOUND);
    }

    return mapAndFetchCrewMembers(flightOptional.get());
  }

  @PostMapping("/credit_cards")
  @ResponseStatus(HttpStatus.CREATED)
  public BankAccountResponseDto createFlight(@Valid @RequestBody BankAccountForm form) {
    BankAccount toSave = modelMapper.map(form, BankAccount.class);
    BankAccount response = bankAccountService.saveBankAccount(toSave);

    //we don't use mapAndFetchCrewMembers(), because crew members list is empty after creation
    return modelMapper.map(response, BankAccountResponseDto.class);
  }

  @PutMapping("/credit_cards/{flight-id}")
  public BankAccountResponseDto updateFlight(@PathVariable("flight-id") Long id,
                                             @Valid @RequestBody BankAccountForm form) {
    if (!bankAccountService.existsById(id)) {
      throw new NotFoundException(NotFoundException.FLIGHT_NOT_FOUND);
    }

    BankAccount toUpdate = modelMapper.map(form, BankAccount.class);
    toUpdate.setId(id);

    BankAccount response = bankAccountService.updateBankAccount(toUpdate);

    return mapAndFetchCrewMembers(response);
  }

  @DeleteMapping("/credit_cards/{flight-id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteFlight(@PathVariable("flight-id") Long id) {
    boolean isDeleted = bankAccountService.deleteBankAccountById(id);

    if (!isDeleted) {
      throw new NotFoundException(NotFoundException.FLIGHT_NOT_FOUND);
    }
  }

  @PostMapping("/credit_cards/{flight-id}/crew-members")
  @ResponseStatus(HttpStatus.CREATED)
  public BankAccountResponseDto linkUpCrewMember(@PathVariable("flight-id") Long flightId,
                                                 @Valid @RequestBody IdToLinkUpDto crewMemberId) {
    //existence validation is here
    linkService.linkUpUserAndBankAccount(crewMemberId.getIdToLink(), flightId);

    Optional<BankAccount> flightOptional = bankAccountService.findBankAccountById(flightId);

    return mapAndFetchCrewMembers(flightOptional.get());
  }

  @DeleteMapping("/credit_cards/{flight-id}/crew-members/{crew-member-id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unlinkUpCrewMember(@PathVariable("flight-id") Long flightId,
                                 @PathVariable("crew-member-id") Long crewMemberId) {
    linkService.unlinkUpUserAndCreditCard(crewMemberId, flightId);
  }

  private BankAccountResponseDto mapAndFetchCrewMembers(BankAccount bankAccount) {
    List<User> userEntities = bankAccountService.findUserOfBankAccount(bankAccount);
    List<UserWithoutAccountsDto> crewMembersDtos = userEntities.stream()
        .map(e -> modelMapper.map(e, UserWithoutAccountsDto.class))
        .toList();

    BankAccountResponseDto resultDto = modelMapper.map(bankAccount, BankAccountResponseDto.class);
    resultDto.setUsers(crewMembersDtos);

    return resultDto;
  }

}