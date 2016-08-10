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
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardListHeaderAdapter;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.HidingScrollListener;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.cell.CardStackCell;
import com.worldventures.dreamtrips.wallet.ui.widget.SmartCardWidget;

import java.util.List;

import butterknife.InjectView;

public class CardListScreen extends WalletFrameLayout<CardListScreenPresenter.Screen, CardListScreenPresenter, CardListPath>
        implements CardListScreenPresenter.Screen {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @InjectView(R.id.appbar)
    AppBarLayout appbar;
    @InjectView(R.id.bankCardList)
    RecyclerView bankCardList;
    @InjectView(R.id.add_credit_list)
    View addCreditList;
    @InjectView(R.id.add_debit_list)
    View addDebitList;
    @InjectView(R.id.main_content)
    CoordinatorLayout mainContent;
    @InjectView(R.id.wallet_list_buttons_wrapper)
    View buttonsWrapper;
    @InjectView(R.id.widget_dashboard_smart_card)
    SmartCardWidget smartCardWidget;

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
    protected void onFinishInflate() {
        super.onFinishInflate();
        toolbar.setTitle(R.string.wallet);
        toolbar.setNavigationOnClickListener(this::onNavigateButtonClick);
        toolbar.inflateMenu(R.menu.menu_wallet_dashboard);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);
    }

    private boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_card_settings:
                presenter.goToSettings();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onPostAttachToWindowView() {
        adapter = new BaseDelegateAdapter(getContext(), getInjector());
        adapter.registerCell(CardStackViewModel.class, CardStackCell.class);
        adapter.registerDelegate(CardStackViewModel.class, new CardStackCell.Delegate() {
            @Override
            public void onCardClicked(BankCard bankCard) {
                getPresenter().showBankCardDetails(bankCard);
            }

            @Override
            public void onCellClicked(CardStackViewModel model) {

            }
        });
        adapter.registerIdDelegate(CardStackViewModel.class, model -> {
            return ((CardStackViewModel) model).getHeaderTitle().hashCode();
        });

        bankCardList.setAdapter(adapter);
        bankCardList.setItemAnimator(new DefaultItemAnimator());
        bankCardList.addItemDecoration(getStickyHeadersItemDecoration(adapter));
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        layout.setAutoMeasureEnabled(true);
        bankCardList.setLayoutManager(layout);
        bankCardList.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                buttonsWrapper.setVisibility(GONE);
            }

            @Override
            public void onShow() {
                buttonsWrapper.setVisibility(VISIBLE);
            }
        });
    }

    @Override
    public void showRecordsInfo(List<CardStackViewModel> result) {
        adapter.clearAndUpdateItems(result);
        smartCardWidget.bindCount(result);
    }

    @Override
    public void showSmartCardInfo(SmartCard smartCard) {
        smartCardWidget.bindCard(smartCard);
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