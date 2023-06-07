package com.vernik03.payment.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bank_accounts")
public class BankAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "card_number", nullable = false)
  private String card_number;

  @Column(name = "balance", nullable = false)
  private Double balance;

  @Column(name = "is_blocked", nullable = false)
  private Boolean is_blocked;

  @ManyToOne()
  @JoinColumn(name = "fk_user_id", referencedColumnName = "id")
  private User user;

  @PreRemove
  private void removeFlightFromCrewMembers() {
    user.getBankAccounts().remove(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BankAccount bankAccount = (BankAccount) o;

    return Objects.equals(id, bankAccount.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

}
