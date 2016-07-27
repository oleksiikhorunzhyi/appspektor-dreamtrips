package com.worldventures.dreamtrips.wallet.domain.entity.card;

import android.support.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
public interface BankCard extends Card {
    @Nullable String title();

    @Nullable CARD_TYPE cardType();

    @Nullable String type();

    enum CARD_TYPE {
        DEBIT, CREDIT
    }
}