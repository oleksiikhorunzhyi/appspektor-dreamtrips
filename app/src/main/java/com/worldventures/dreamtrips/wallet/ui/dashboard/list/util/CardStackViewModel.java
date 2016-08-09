package com.worldventures.dreamtrips.wallet.ui.dashboard.list.util;

import android.support.annotation.Nullable;

import com.techery.spares.adapter.HeaderItem;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import java.util.List;

public class CardStackViewModel implements HeaderItem {

    private String name;
    private List<BankCard> bankCards;

    public CardStackViewModel(String name, List<BankCard> bankCard) {
        this.name = name;
        this.bankCards = bankCard;
    }

    public List<BankCard> getBankCards() {
        return bankCards;
    }

    @Nullable @Override public String getHeaderTitle() {
        return name;
    }
}
