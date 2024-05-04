package com.shepherdmoney.interviewproject.controller;

import com.shepherdmoney.interviewproject.model.BalanceHistory;
import com.shepherdmoney.interviewproject.model.CreditCard;
import com.shepherdmoney.interviewproject.model.User;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;

import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class CreditCardController {

    private CreditCardRepository creditCardRepository;
    private UserRepository userRepository;

    @PostMapping("/credit-card")
    public ResponseEntity<Integer> addCreditCardToUser(@RequestBody AddCreditCardToUserPayload payload) {
        //       Return 200 OK with the credit card id if the user exists and credit card is successfully associated with the user
        //       Return other appropriate response code for other exception cases
        //       Do not worry about validating the card number, assume card number could be any arbitrary format and length
        User user = userRepository.findById(payload.getUserId()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber(payload.getCardNumber());
        creditCard.setIssuanceBank(payload.getCardIssuanceBank());

        user.addCreditCard(creditCard);
        creditCardRepository.save(creditCard);
        
        return ResponseEntity.ok(creditCard.getId());
    }

    @GetMapping("/credit-card:all")
    public ResponseEntity<List<CreditCardView>> getAllCardOfUser(@RequestParam int userId) {
        //       if the user has no credit card, return empty list, never return null
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        List<CreditCard> creditCards = user.getCreditCards();
        List<CreditCardView> creditCardViews = creditCards.stream()
                .map(card -> new CreditCardView(card.getId(), card.getIssuanceBank(), card.getCardNumber()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(creditCardViews);
    }

    @GetMapping("/credit-card:user-id")
    public ResponseEntity<Integer> getUserIdForCreditCard(@RequestParam String creditCardNumber) {
        //       If so, return the user id in a 200 OK response. If no such user exists, return 400 Bad Request
        CreditCard creditCard = creditCardRepository.findByNumber(creditCardNumber);

        if (creditCard == null || creditCard.getOwner() == null) {
            // If the credit card or user doesn't exist, return a 400 Bad Request response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Return the user ID associated with the credit card
        return ResponseEntity.ok(creditCard.getOwner().getId());
    }

    @PostMapping("/credit-card:update-balance")
    public SomeEnityData postMethodName(@RequestBody UpdateBalancePayload[] payload) {
        //      1. For the balance history in the credit card
        //      2. If there are gaps between two balance dates, fill the empty date with the balance of the previous date
        //      3. Given the payload `payload`, calculate the balance different between the payload and the actual balance stored in the database
        //      4. If the different is not 0, update all the following budget with the difference
        //      For example: if today is 4/12, a credit card's balanceHistory is [{date: 4/12, balance: 110}, {date: 4/10, balance: 100}],
        //      Given a balance amount of {date: 4/11, amount: 110}, the new balanceHistory is
        //      [{date: 4/12, balance: 120}, {date: 4/11, balance: 110}, {date: 4/10, balance: 100}]
        //      This is because
        //      1. You would first populate 4/11 with previous day's balance (4/10), so {date: 4/11, amount: 100}
        //      2. And then you observe there is a +10 difference
        //      3. You propagate that +10 difference until today
        //      Return 200 OK if update is done and successful, 400 Bad Request if the given card number
        //        is not associated with a card.
        
        SomeEnityData result = new SomeEnityData();
        Map<String, String> updateResults = new HashMap<>();
        for (UpdateBalancePayload transaction : payload) {
            CreditCard creditCard = creditCardRepository.findByNumber(transaction.getCreditCardNumber());

            if (creditCard == null) {
                updateResults.put(transaction.getCreditCardNumber(), "Credit card not found");
                continue; // Skip to the next payload item
            }

            // Find the relevant balance history entry, or create a new one
            List<BalanceHistory> balanceHistory = creditCard.getBalanceHistory();
            BalanceHistory previousEntry = balanceHistory.stream()
                    .filter(bh -> bh.getDate().equals(transaction.getBalanceDate()))
                    .findFirst()
                    .orElse(null);

            double previousBalance = previousEntry != null ? previousEntry.getBalance() : 0;
            double difference = transaction.getBalanceAmount() - previousBalance;

            if (difference != 0) {
                boolean updateNextEntries = false;

                for (BalanceHistory bh : balanceHistory) {
                    if (bh.getDate().equals(transaction.getBalanceDate())) {
                        bh.setBalance(transaction.getBalanceAmount());
                        updateNextEntries = true;
                    } else if (updateNextEntries) {
                        bh.setBalance(bh.getBalance() + difference);
                    }
                }

                // Create a new BalanceHistory if there's no previous entry for the specific date
                if (previousEntry == null) {
                    BalanceHistory newEntry = new BalanceHistory();
                    newEntry.setDate(transaction.getBalanceDate());
                    newEntry.setBalance(transaction.getBalanceAmount());
                    balanceHistory.add(newEntry);
                }
            }
            creditCardRepository.save(creditCard);
            updateResults.put(transaction.getCreditCardNumber(), "Updated successfully");
        }
        result.setUpdateResults(updateResults);
        return result;
    }
}
