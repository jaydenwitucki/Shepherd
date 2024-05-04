package com.shepherdmoney.interviewproject.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String issuanceBank;

    private String cardNumber;


    private User owner;
    public void setOwner(User user){

    }


    private ArrayList<BalanceHistory> balanceHistory = new ArrayList<>();

    // Some field here <> owner;

    private void addBalanceHistory(BalanceHistory entry){
        for (int i = 0; i < balanceHistory.size(); i++) {
            if (entry.getDate().isAfter(balanceHistory.get(i).getDate())) {
                balanceHistory.add(i, entry);
                return;
            }
        }
        balanceHistory.add(entry);
    }
    //       list must be in chronological order, with the most recent date appearing first in the list. 
    //       Additionally, the last object in the "list" must have a date value that matches today's date, 
    //       since it represents the current balance of the credit card.
    //       This means that if today is 04-16, and the list begin as empty, you receive a payload for 04-13,
    //       you should fill the list up until 04-16. For example:
    //       [
    //         {date: '2023-04-10', balance: 800},
    //         {date: '2023-04-11', balance: 1000},
    //         {date: '2023-04-12', balance: 1200},
    //         {date: '2023-04-13', balance: 1100},
    //         {date: '2023-04-16', balance: 900},
    //       ]
    // ADDITIONAL NOTE: For the balance history, you can use any data structure that you think is appropriate.
    //        It can be a list, array, map, pq, anything. However, there are some suggestions:
    //        1. Retrieval of a balance of a single day should be fast
    //        2. Traversal of the entire balance history should be fast
    //        3. Insertion of a new balance should be fast
    //        4. Deletion of a balance should be fast
    //        5. It is possible that there are gaps in between dates (note the 04-13 and 04-16)
    //        6. In the condition that there are gaps, retrieval of "closest **previous**" balance date should also be fast. Aka, given 4-15, return 4-13 entry tuple

    public void populateMissingData(LocalDate date, double balance){
        LocalDate today = LocalDate.now();
        while (!date.isAfter(today)) {
            BalanceHistory newEntry = new BalanceHistory();
            newEntry.setDate(date);
            newEntry.setBalance(balance);
            addBalanceHistory(newEntry);
            date = date.plusDays(1);
        }
    }

    public void setNumber(String cardNumber) {
        
        this.cardNumber = cardNumber;
    }
}
