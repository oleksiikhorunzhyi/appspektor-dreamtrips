package com.worldventures.dreamtrips.wallet.domain.entity.card;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

import io.techery.janet.smartcard.model.Record;

@Value.Immutable
public interface BankCard extends Card {
    @Nullable
    String title();

    @Nullable
    CardType cardType();

    Record.FinancialService type();

    int cvv();

    enum CardType {
        DEBIT, CREDIT
    }
}