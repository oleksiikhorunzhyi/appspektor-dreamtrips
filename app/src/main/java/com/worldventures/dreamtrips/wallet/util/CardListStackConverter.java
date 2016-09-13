package com.worldventures.dreamtrips.wallet.util;


import android.content.Context;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.BankCardViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand.CardStackModel.StackType.PAYMENT;

public class CardListStackConverter {

   private final Context context;

   public CardListStackConverter(Context context) {
      this.context = context;
   }
   public List<CardStackViewModel> convertToModelViews(List<CardStacksCommand.CardStackModel> stackList) {
      List<CardStackViewModel> list = new ArrayList<>(stackList.size());

      for (CardStacksCommand.CardStackModel vm: stackList){
         switch (vm.stackStackType) {
            case PAYMENT:
               list.add(obtainPaymentCardsStack(vm.bankCards, vm.defaultCardId));
               break;
            case LOYALTY:
               break;
         }
      }

      return list;
   }

   private CardStackViewModel obtainPaymentCardsStack(List<BankCard> bankCards, String defaultCardId) {
      List<BankCardViewModel> bankCardViewModels = Queryable.from(bankCards)
            .map(element -> new BankCardViewModel(element, CardUtils.isRealCardId(defaultCardId) && TextUtils.equals(element.id(), defaultCardId)))
            .toList();

      return new CardStackViewModel(PAYMENT, bankCardViewModels, context.getString(R.string.wallet_payment_cards_title));
   }

}
