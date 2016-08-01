package com.worldventures.dreamtrips.wallet.domain.entity.card;

import android.support.annotation.Nullable;

import java.util.Date;

public interface Card {
    @Nullable String id();

    @Nullable String scid();

    @Nullable String number();

    @Nullable Category category();

    @Nullable Date endDate();

    enum Category {
        BANK, DISCOUNT
    }
}