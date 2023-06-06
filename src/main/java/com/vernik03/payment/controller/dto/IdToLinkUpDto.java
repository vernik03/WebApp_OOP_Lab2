package com.vernik03.payment.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdToLinkUpDto {

  @NotNull(message = "id_to_link_is_empty:Id to link can not be empty")
  private Long idToLink;

}
