package com.worldventures.dreamtrips.wallet.domain.entity.card;

import org.immutables.value.Value;

@Value.Immutable
public interface BankCard extends Card {
    String bankName();

    CARD_TYPE cardType();

    String type();

    enum CARD_TYPE {
        DEBIT, CREDIT
    }
}