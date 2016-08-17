package com.worldventures.dreamtrips.wallet.ui.dashboard.list.util;

import android.support.annotation.Nullable;

import com.techery.spares.adapter.HeaderItem;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand.CardStackModel.StackType;

import java.util.List;

public class CardStackViewModel implements HeaderItem {
   private StackType type;
   private List<BankCard> cardList;
   private String title;

   public CardStackViewModel(StackType type, List<BankCard> cardList, String title) {
      this.type = type;
      this.cardList = cardList;
      this.title = title;
   }

   public StackType getType() {
      return type;
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
