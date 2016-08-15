package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;

import java.util.Random;

import io.techery.janet.smartcard.model.ImmutableRecord;
import io.techery.janet.smartcard.model.Record;

public class BankCardConverter implements Converter<Record, BankCard> {
    @Override
    public BankCard from(Record record) {
        return ImmutableBankCard.builder()
                .id(String.valueOf(record.id()))
                .title(record.title())
                .cvv(Integer.parseInt(record.cvv()))
                .number(Long.parseLong(record.cardNumber()))
                .type(record.financialService())
                .expiryMonth(record.expiryMonth())
                .expiryYear(record.expiryYear())
                .cardType(generateRandomCardType())
                .build();
    }

    @Override
    public Record to(BankCard card) {
        // TODO: use normal id
        return ImmutableRecord.builder()
                .id(Integer.parseInt(card.id()))
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

    //TODO use real record type instead of random
    private BankCard.CardType generateRandomCardType() {
        return new Random().nextBoolean() ? BankCard.CardType.CREDIT : BankCard.CardType.DEBIT;
    }
}