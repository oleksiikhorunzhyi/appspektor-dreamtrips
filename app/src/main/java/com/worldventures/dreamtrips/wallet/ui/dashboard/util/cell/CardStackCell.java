package com.worldventures.dreamtrips.wallet.ui.dashboard.util.cell;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.BankCardViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.util.OverlapDecoration;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_wallet_cardstack)
public class CardStackCell extends AbstractDelegateCell<CardStackViewModel, CardStackCell.Delegate> {

   @Inject @ForActivity Injector injector;

   @InjectView(R.id.cardStack) RecyclerView cardStack;
   BaseDelegateAdapter adapter;

   private static final double VISIBLE_SCALE = 0.64;

   public CardStackCell(View view) {
      super(view);

      int dimension = itemView.getResources().getDimensionPixelSize(R.dimen.wallet_card_height);
      cardStack.addItemDecoration(new OverlapDecoration((int) (dimension * VISIBLE_SCALE * -1)));
      LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
      layoutManager.setAutoMeasureEnabled(true);
      cardStack.setLayoutManager(layoutManager);
      cardStack.setNestedScrollingEnabled(false);
   }

   @Override
   public void afterInject() {
      super.afterInject();
      BankCardCell.Delegate childCellDelegate = model -> cellDelegate.onCardClicked(model.bankCard);
      adapter = new BaseDelegateAdapter(itemView.getContext(), injector);
      adapter.registerCell(BankCardViewModel.class, BankCardCell.class);
      adapter.registerDelegate(BankCardViewModel.class, childCellDelegate);

      cardStack.setAdapter(adapter);
   }

   @Override
   protected void syncUIStateWithModel() {
      adapter.clearAndUpdateItems(getModelObject().getCardList());
   }

   @Override
   public void prepareForReuse() {

   }

   public static abstract class Delegate implements CellDelegate<CardStackViewModel> {
      public abstract void onCardClicked(BankCard bankCard);

      @Override
      public void onCellClicked(CardStackViewModel model) { }
   }
}

