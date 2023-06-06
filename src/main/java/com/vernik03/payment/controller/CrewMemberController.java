package com.vernik03.payment.controller;

import com.vernik03.payment.controller.dto.IdToLinkUpDto;
import com.vernik03.payment.controller.dto.user.UserForm;
import com.vernik03.payment.controller.dto.user.UserResponseDto;
import com.vernik03.payment.controller.dto.user.UserWithoutAccountsDto;
import com.vernik03.payment.controller.dto.user.UsersListDto;
import com.vernik03.payment.controller.dto.bankaccount.BankAccountsWithoutUserDto;
import com.vernik03.payment.exception.NotFoundException;
import com.vernik03.payment.model.User;
import com.vernik03.payment.model.BankAccount;
import com.vernik03.payment.service.UserService;
import com.vernik03.payment.service.CreditCardService;
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
public class CrewMemberController {

  private final UserService userService;
  private final CreditCardService linkService;
  private final ModelMapper modelMapper;

  @Autowired
  public CrewMemberController(UserService userService,
                              CreditCardService linkService,
                              ModelMapper modelMapper) {
    this.userService = userService;
    this.linkService = linkService;
    this.modelMapper = modelMapper;
  }

  @GetMapping("/crew-members")
  public UsersListDto getListOfCrewMembers() {
    List<User> entities = userService.findAll();

    List<UserWithoutAccountsDto> dtos = entities.stream()
        .map(e -> modelMapper.map(e, UserWithoutAccountsDto.class))
        .toList();

    return new UsersListDto(dtos);
  }

  @GetMapping("/crew-members/{crew-member-id}")
  public UserResponseDto getCrewMemberById(@PathVariable("crew-member-id") Long id) {
    Optional<User> crewMemberOptional = userService.findUserById(id);

    if (crewMemberOptional.isEmpty()) {
      throw new NotFoundException(NotFoundException.CREW_MEMBER_NOT_FOUND);
    }

    return mapAndFetchFlights(crewMemberOptional.get());
  }

  @PostMapping("/crew-members")
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponseDto createCrewMember(@Valid @RequestBody UserForm form) {
    User toSave = modelMapper.map(form, User.class);
    User response = userService.saveUser(toSave);

    //we don't use mapAndFetchFlights(), because credit_cards list is empty after creation
    return modelMapper.map(response, UserResponseDto.class);
  }

  @PutMapping("/crew-members/{crew-member-id}")
  public UserResponseDto updateCrewMember(@PathVariable("crew-member-id") Long id,
                                          @Valid @RequestBody UserForm form) {
    if (!userService.existsById(id)) {
      throw new NotFoundException(NotFoundException.CREW_MEMBER_NOT_FOUND);
    }

    User toUpdate = modelMapper.map(form, User.class);
    toUpdate.setId(id);

    User response = userService.updateUser(toUpdate);

    return mapAndFetchFlights(response);
  }

  @DeleteMapping("/crew-members/{crew-member-id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteCrewMember(@PathVariable("crew-member-id") Long id) {
    boolean isDeleted = userService.deleteUserById(id);

    if (!isDeleted) {
      throw new NotFoundException(NotFoundException.CREW_MEMBER_NOT_FOUND);
    }
  }

  @PostMapping("/crew-members/{crew-member-id}/credit_cards")
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponseDto linkUpFlight(@PathVariable("crew-member-id") Long crewMemberId,
                                      @Valid @RequestBody IdToLinkUpDto flightId) {
    //existence validation is here
    linkService.linkUpUserAndBankAccount(crewMemberId, flightId.getIdToLink());

    Optional<User> crewMemberOptional = userService.findUserById(crewMemberId);

    return mapAndFetchFlights(crewMemberOptional.get());
  }

  @DeleteMapping("/crew-members/{crew-member-id}/credit_cards/{flight-id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unlinkUpFlight(@PathVariable("crew-member-id") Long crewMemberId,
                             @PathVariable("flight-id") Long flightId) {
    linkService.unlinkUpUserAndCreditCard(crewMemberId, flightId);
  }

  private UserResponseDto mapAndFetchFlights(User user) {
    List<BankAccount> bankAccountEntities
        = userService.findBankAccountsOfUser(user);
    List<BankAccountsWithoutUserDto> flightDtos = bankAccountEntities.stream()
        .map(e -> modelMapper.map(e, BankAccountsWithoutUserDto.class))
        .toList();

    UserResponseDto resultDto
        = modelMapper.map(user, UserResponseDto.class);
    resultDto.setBank_accounts(flightDtos);

    return resultDto;
  }

}