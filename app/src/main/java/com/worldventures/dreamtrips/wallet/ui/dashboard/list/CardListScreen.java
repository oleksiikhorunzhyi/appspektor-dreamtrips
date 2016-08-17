package com.worldventures.dreamtrips.wallet.ui.dashboard.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardListHeaderAdapter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.HidingScrollListener;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.cell.CardStackCell;
import com.worldventures.dreamtrips.wallet.ui.widget.SmartCardWidget;
import com.worldventures.dreamtrips.wallet.util.CardUtils;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observable;

import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;
import static android.support.design.widget.AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED;

public class CardListScreen extends WalletFrameLayout<CardListScreenPresenter.Screen, CardListScreenPresenter, CardListPath> implements CardListScreenPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
   @InjectView(R.id.appbar) AppBarLayout appbar;
   @InjectView(R.id.bankCardList) RecyclerView bankCardList;
   @InjectView(R.id.main_content) CoordinatorLayout mainContent;
   @InjectView(R.id.wallet_list_buttons_wrapper) View buttonsWrapper;
   @InjectView(R.id.widget_dashboard_smart_card) SmartCardWidget smartCardWidget;
   @InjectView(R.id.empty_view_text) View emptyCardListView;

   private BaseDelegateAdapter adapter;

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
      setupToolbar();
      setupCardStackList();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return new DialogOperationScreen(this);
   }

   @Override
   public void showRecordsInfo(List<CardStackViewModel> result) {
      int cardCount = CardUtils.stacksToItemsCount(result);

      adapter.clearAndUpdateItems(result);
      smartCardWidget.bindCount(cardCount);

      bankCardList.setVisibility(cardCount == 0 ? GONE : VISIBLE);
      emptyCardListView.setVisibility(cardCount == 0 ? VISIBLE : GONE);

      if (cardCount == 0) showEmptyCardListView();
      else showCardList();
   }

   private void showCardList() {
      bankCardList.setVisibility(VISIBLE);
      emptyCardListView.setVisibility(GONE);

      AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
      params.setScrollFlags(SCROLL_FLAG_SCROLL | SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
      collapsingToolbar.setLayoutParams(params);
   }

   private void showEmptyCardListView() {
      bankCardList.setVisibility(GONE);
      emptyCardListView.setVisibility(VISIBLE);

      AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbar.getLayoutParams();
      params.setScrollFlags(0);
      collapsingToolbar.setLayoutParams(params);
   }

   @Override
   public void showSmartCardInfo(SmartCard smartCard) {
      smartCardWidget.bindCard(smartCard);
   }

   @Override
   public Observable<Boolean> lockStatus() {
      return smartCardWidget.lockStatus();
   }

   @Override
   public Observable<Void> unSupportedUnlockOperation() {
      return smartCardWidget.unSupportedUnlockOperation();
   }

   @Override
   public void disableLockBtn() {
      smartCardWidget.setLockBtnEnabled(false);
   }

   private void onNavigateButtonClick(View view) {
      presenter.navigationClick();
   }

   private void setupToolbar() {
      toolbar.setTitle(R.string.wallet);
      toolbar.setNavigationOnClickListener(this::onNavigateButtonClick);
      toolbar.inflateMenu(R.menu.menu_wallet_dashboard);
      toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
   }

   private void setupCardStackList() {
      adapter = new BaseDelegateAdapter(getContext(), getInjector());
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
      presenter.addCreditCard();
   }

   @OnClick(R.id.add_debit_list)
   protected void addDebitCardClick() {
      presenter.addDebitCard();
   }

   private boolean onMenuItemClick(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_card_settings:
            presenter.onSettingsChosen();
            return true;
         default:
            return false;
      }
   }
}