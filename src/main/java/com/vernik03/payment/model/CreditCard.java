package com.vernik03.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.Data;

@Data
@Entity
@Table(name = "credit_cards")
public class CreditCard {

  @Column(name = "fk_user_id")
  private Long user_id;

  @Id
  @Column(name = "fk_bank_account_id")
  private Long bank_account_id;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CreditCard user = (CreditCard) o;

    return Objects.equals(user_id, user.user_id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bank_account_id);
  }

}
