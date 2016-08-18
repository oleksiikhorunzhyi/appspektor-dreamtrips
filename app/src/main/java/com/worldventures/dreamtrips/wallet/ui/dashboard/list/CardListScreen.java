package com.worldventures.dreamtrips.wallet.ui.dashboard.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardListHeaderAdapter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.HidingScrollListener;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.ImmutableCardStackHeaderHolder;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.cell.CardStackCell;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.cell.CardStackHeaderCell;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

public class CardListScreen extends WalletFrameLayout<CardListScreenPresenter.Screen, CardListScreenPresenter, CardListPath> implements CardListScreenPresenter.Screen {

   @InjectView(R.id.bankCardList) RecyclerView bankCardList;
   @InjectView(R.id.wallet_list_buttons_wrapper) View buttonsWrapper;
   @InjectView(R.id.add_debit_list) View addDebitCard;
   @InjectView(R.id.add_credit_list) View addCreditCard;
   @InjectView(R.id.empty_view_text) View emptyCardListView;

   private IgnoreFirstItemAdapter adapter;

   public CardListScreen(Context context) {
      super(context);
   }

   public CardListScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @NonNull
   @Override
   public CardListScreenPresenter createPresenter() {
      return new CardListScreenPresenter(getContext(), getInjector());
   }

   @Override
   protected void onPostAttachToWindowView() {
      setupCardStackList();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   public void showRecordsInfo(List<CardStackViewModel> result) {
      adapter.clear();
      adapter.addItems(result);
      emptyCardListView.setVisibility(adapter.getCount() == 1 ? VISIBLE : GONE);
   }

   @Override
   public void notifySmartCardChanged(CardStackHeaderHolder cardStackHeaderHolder) {
      Object header = Queryable.from(adapter.getItems()).firstOrDefault(it -> it instanceof CardStackHeaderHolder);
      if (header != null) {
         adapter.remove(header);
      }
      adapter.addItem(0, cardStackHeaderHolder);
      adapter.notifyDataSetChanged();
   }

   @Override
   public void setEnableAddingCardButtons(boolean enabled) {
      addDebitCard.setEnabled(enabled);
      addCreditCard.setEnabled(enabled);
   }

   private void setupCardStackList() {
      adapter = new IgnoreFirstItemAdapter(getContext(), getInjector());
      adapter.registerCell(CardStackViewModel.class, CardStackCell.class);
      adapter.registerDelegate(CardStackViewModel.class, new CardStackCell.Delegate() {
         @Override
         public void onCardClicked(BankCard bankCard) {
            getPresenter().showBankCardDetails(bankCard);
         }
      });
      adapter.registerIdDelegate(CardStackViewModel.class, model -> {
         CardStackViewModel vm = ((CardStackViewModel) model);
         return vm.getHeaderTitle() != null ? vm.getHeaderTitle().hashCode() : 0;
      });

      adapter.registerCell(ImmutableCardStackHeaderHolder.class, CardStackHeaderCell.class);
      adapter.registerDelegate(ImmutableCardStackHeaderHolder.class, new CardStackHeaderCell.Delegate() {
         @Override
         public void onCellClicked(CardStackHeaderHolder model) {

         }

         @Override
         public void onSettingsChosen() {
            presenter.onSettingsChosen();
         }

         @Override
         public void onNavigateButtonClick() {
            presenter.navigationClick();
         }

         @Override
         public void onLockChanged(boolean isLocked) {
            presenter.onLockChanged(isLocked);
         }
      });

      bankCardList.setAdapter(adapter);
      bankCardList.setItemAnimator(new DefaultItemAnimator());
      bankCardList.addItemDecoration(getStickyHeadersItemDecoration(adapter));
      LinearLayoutManager layout = new LinearLayoutManager(getContext());
      layout.setAutoMeasureEnabled(true);
      bankCardList.setLayoutManager(layout);
      bankCardList.addOnScrollListener(new HidingScrollListener() {
         @Override
         public void onVisibilityChangeRequested(boolean show) {
            buttonsWrapper.setVisibility(show ? VISIBLE : GONE);
         }
      });
   }

   private StickyHeadersItemDecoration getStickyHeadersItemDecoration(BaseArrayListAdapter adapter) {
      return new StickyHeadersBuilder().setAdapter(adapter)
            .setRecyclerView(bankCardList)
            .setStickyHeadersAdapter(new CardListHeaderAdapter(adapter.getItems()), false)
            .build();
   }

   @OnClick(R.id.add_credit_list)
   protected void addCreditCardClick() {
      presenter.addCardRequired(BankCard.CardType.CREDIT);
   }

   @OnClick(R.id.add_debit_list)
   protected void addDebitCardClick() {
      presenter.addCardRequired(BankCard.CardType.DEBIT);
   }
}