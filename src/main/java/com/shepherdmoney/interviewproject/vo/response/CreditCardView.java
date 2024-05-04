package com.shepherdmoney.interviewproject.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreditCardView {

    private String issuanceBank;

    private String number;
    
    private int id;

    public CreditCardView(int id, String issuanceBank, String number) {
        this.id = id;
        this.issuanceBank = issuanceBank;
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public String getIssuanceBank() {
        return issuanceBank;
    }

    public String getNumber() {
        return number;
    }

}
