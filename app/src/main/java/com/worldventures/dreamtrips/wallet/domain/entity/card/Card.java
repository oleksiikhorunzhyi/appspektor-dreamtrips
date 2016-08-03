package com.worldventures.dreamtrips.wallet.domain.entity.card;

import android.support.annotation.Nullable;

public interface Card {
    @Nullable
    String id();

    @Nullable
    String scid(); //smart card id

    long number();

    int expiryMonth();

    int expiryYear();

    @Nullable
    Category category();

    enum Category {
        BANK, DISCOUNT
    }
}