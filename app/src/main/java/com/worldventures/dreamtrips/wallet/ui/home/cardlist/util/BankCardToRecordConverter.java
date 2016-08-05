package com.worldventures.dreamtrips.wallet.ui.home.cardlist.util;

import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;

import java.util.Random;

import io.techery.janet.smartcard.model.ImmutableRecord;
import io.techery.janet.smartcard.model.Record;

public class BankCardToRecordConverter  {

     public Record convert(BankCard card) {
         // TODO: use normal id
        return ImmutableRecord.builder()
                .id((byte) 1)
                .title(card.title())
                .cardNumber(String.valueOf(card.number()))
                .cvv(String.valueOf(card.cvv()))
                .expiryMonth(card.expiryMonth())
                .expiryYear(card.expiryYear())
                .financialService(Record.FinancialService.MASTERCARD)
                .t1("T1??")
                .t2("T2??")
                .t3("T3??")
                .build();
    }

    public BankCard convert(Record record) {
        return ImmutableBankCard.builder()
                .title(record.title())
                .cvv(Integer.parseInt(record.cvv()))
                .number(Long.parseLong(record.cardNumber()))
                .type(record.financialService())
                .expiryMonth(record.expiryMonth())
                .expiryYear(record.expiryYear())
                .cardType(generateRandomCardType())
                .build();
    }

    //TODO use real record type instead of random
    private BankCard.CardType generateRandomCardType() {
        return new Random().nextBoolean() ? BankCard.CardType.CREDIT : BankCard.CardType.DEBIT;
    }
}
