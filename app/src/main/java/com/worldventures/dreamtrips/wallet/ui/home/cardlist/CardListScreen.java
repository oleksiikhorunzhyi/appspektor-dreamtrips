package com.worldventures.dreamtrips.wallet.ui.home.cardlist;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletFrameLayout;
import com.worldventures.dreamtrips.wallet.ui.home.cardlist.util.CardListHeaderAdapter;
import com.worldventures.dreamtrips.wallet.ui.home.cardlist.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.home.cardlist.util.HidingScrollListener;
import com.worldventures.dreamtrips.wallet.ui.home.cardlist.util.cell.CardStackCell;
import com.worldventures.dreamtrips.wallet.ui.settings.card_details.CardDetailsPath;

import java.util.List;

import butterknife.InjectView;
import flow.Flow;

public class CardListScreen extends WalletFrameLayout<CardListScreenPresenter.Screen, CardListScreenPresenter, CardListPath>
        implements CardListScreenPresenter.Screen {

    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @InjectView(R.id.appbar) AppBarLayout appbar;
    @InjectView(R.id.bankCardList) RecyclerView bankCardList;
    @InjectView(R.id.add_credit_list) View addCreditList;
    @InjectView(R.id.add_debit_list) View addDebitList;
    @InjectView(R.id.main_content) CoordinatorLayout mainContent;
    @InjectView(R.id.wallet_list_buttons_wrapper) View buttonsWrapper;
    private BaseDelegateAdapter adapter;

    public CardListScreen(Context context) {
        super(context);
    }

    public CardListScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public CardListScreenPresenter createPresenter() {
        return new CardListScreenPresenter(getContext(), getInjector());
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(isInEditMode()) return;
        adapter = new BaseDelegateAdapter(getContext(), getInjector());
        adapter.registerCell(CardStackViewModel.class, CardStackCell.class);
        adapter.registerDelegate(CardStackViewModel.class, new CardStackCell.Delegate() {
            @Override public void onCardClicked(BankCard bankCard) {
                Flow.get(getContext()).set(new CardDetailsPath());
            }

            @Override public void onCellClicked(CardStackViewModel model) {

            }
        });

        adapter.setHasStableIds(true);

        bankCardList.setAdapter(adapter);
        bankCardList.setItemAnimator(new DefaultItemAnimator());
        bankCardList.addItemDecoration(getStickyHeadersItemDecoration(adapter));
        bankCardList.setLayoutManager(new LinearLayoutManager(getContext()));
        bankCardList.addOnScrollListener(new HidingScrollListener() {
            @Override public void onHide() {
                buttonsWrapper.setVisibility(GONE);
            }

            @Override public void onShow() {
                buttonsWrapper.setVisibility(VISIBLE);
            }
        });
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        toolbar.setTitle(R.string.wallet);
        toolbar.setNavigationOnClickListener(this::onNavigateButtonClick);
    }

    @Override public void onListReceived(List<CardStackViewModel> result) {
        adapter.addItems(result);
    }

    private void onNavigateButtonClick(View view) {
        presenter.goToBack();
    }

    private StickyHeadersItemDecoration getStickyHeadersItemDecoration(BaseArrayListAdapter adapter) {
        return new StickyHeadersBuilder()
                .setAdapter(adapter)
                .setRecyclerView(bankCardList)
                .setStickyHeadersAdapter(new CardListHeaderAdapter(adapter.getItems()), false)
                .build();
    }

}