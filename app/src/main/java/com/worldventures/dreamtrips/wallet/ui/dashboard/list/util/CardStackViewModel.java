package com.worldventures.dreamtrips.wallet.ui.dashboard.list.util;

import android.support.annotation.Nullable;

import com.techery.spares.adapter.HeaderItem;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

import java.util.List;

public class CardStackViewModel implements HeaderItem {
   private String title;

   private List<BankCard> cardList;

   public CardStackViewModel(String title, List<BankCard> cardList) {
      this.title = title;
      this.cardList = cardList;
   }

   public List<BankCard> getCardList() {
      return cardList;
   }

   @Nullable
   @Override
   public String getHeaderTitle() {
      return title;
   }
}
