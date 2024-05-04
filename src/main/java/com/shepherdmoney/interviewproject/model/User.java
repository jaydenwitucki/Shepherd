package com.shepherdmoney.interviewproject.model;

import java.util.ArrayList;

//import org.hibernate.mapping.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "MyUser")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    private String email;

    // HINT: A user can have one or more, or none at all. We want to be able to query credit cards by user
    //       and user by a credit card.
    private ArrayList<CreditCard> creditCards = new ArrayList<>();

    public void addCreditCard(CreditCard card){
        card.setOwner(this);
        creditCards.add(card);
    }

}
