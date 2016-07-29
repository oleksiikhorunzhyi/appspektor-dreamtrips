package com.worldventures.dreamtrips.wallet.domain.entity.card;

import java.util.Date;

public interface Card {
    String id();

    String scid();

    String number();

    Category category();

    Date endDate();

    enum Category {
        BANK, DISCOUNT
    }
}