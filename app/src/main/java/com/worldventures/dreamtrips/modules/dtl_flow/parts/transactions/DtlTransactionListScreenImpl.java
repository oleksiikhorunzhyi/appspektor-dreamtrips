package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.listener.ScrollEventListener;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlLayout;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.views.TransactionView;

import java.util.List;

import butterknife.InjectView;
import flow.Flow;
import icepick.State;

public class DtlTransactionListScreenImpl extends DtlLayout<DtlTransactionListScreen, DtlTransactionListPresenter, DtlTransactionListPath>
      implements DtlTransactionListScreen {

   @InjectView(R.id.toolbar_actionbar) Toolbar toolbar;
   @InjectView(R.id.progress_loader) ProgressBar loader;
   @InjectView(R.id.tv_title) TextView tvTitle;
   @InjectView(R.id.emptyView) View emptyView;
   @InjectView(R.id.errorView) View errorView;
   @InjectView(R.id.container_transaction_list) TransactionView transactionsView;
   @InjectView(R.id.dtlToolbarMerchantSearchInput) SearchView searchView;

   @State String searchQuery;

   public DtlTransactionListScreenImpl(Context context) {
      super(context);
   }

   public DtlTransactionListScreenImpl(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public DtlTransactionListPresenter createPresenter() {
      return new DtlTransactionListPresenterImpl(getContext(), injector);
   }

   @Override
   protected void onPostAttachToWindowView() {
      inflateToolbarMenu(toolbar);
      if (ViewUtils.isTabletLandscape(getContext())) {
         toolbar.setBackgroundColor(Color.WHITE);
         tvTitle.setVisibility(View.VISIBLE);
         tvTitle.setText(getContext().getResources().getString(R.string.dtl_show_transaction_toolbar));
      } else {
         toolbar.setTitle(getContext().getResources().getString(R.string.dtl_show_transaction_toolbar));
         toolbar.setNavigationIcon(ViewUtils.isTabletLandscape(getContext()) ? R.drawable.back_icon_black : R.drawable.back_icon);
         toolbar.setNavigationOnClickListener(view -> {
            Flow.get(getContext()).goBack();
         });
      }
      //      setupSearch(); //Disabled in current RC
      searchView.setVisibility(View.GONE);
   }

   @Override
   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      searchQuery = null;
   }

   private void setupSearch() {
      searchView.setVisibility(View.GONE);
      searchView.setIconifiedByDefault(false);
      searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
         @Override
         public boolean onQueryTextSubmit(String query) {
            getAllTransactionsFromAPI(query);
            return true;
         }

         @Override
         public boolean onQueryTextChange(String newText) {
            getAllTransactionsFromAPI(newText);
            return true;
         }
      });
      searchView.setOnCloseListener(() -> {
         transactionsView.setPageableAdapter();
         searchQuery = null;
         return true;
      });
   }

   private void getAllTransactionsFromAPI(String query) {
      searchQuery = query;
      if (query.length() > 2) {
         transactionsView.setSearchableAdapter();
         getPresenter().getAllTransactionsToQuery(query);
      } else {
         transactionsView.setPageableAdapter();
         transactionsView.setNotLoading();
      }
   }

   @Override
   public void searchQuery(String query) {
      transactionsView.setSearchableAdapter();
      transactionsView.filterByMerchantName(query);
   }

   @Override
   public void setEventListener(ScrollEventListener listener) {
      transactionsView.setScrollEventListener(listener);
   }

   @Override
   public void setAllTransactions(List<TransactionModel> transactions) {
      transactionsView.setAllTransactionsList(transactions);
      showSearchIfNeeded();
   }

   private void showSearchIfNeeded() {
      if (!TextUtils.isEmpty(searchQuery) && searchQuery.length() > 2) {
         transactionsView.setSearchableAdapter();
         searchView.setQuery(searchQuery, true);
      }
   }

   @Override
   public void addTransactions(List<TransactionModel> transactions) {
      transactionsView.addItems(transactions);
      showSearchIfNeeded();
   }

   @Override
   public void onRefreshSuccess(boolean searchMode) {
      loader.setVisibility(View.GONE);
      transactionsView.setVisibility(View.VISIBLE);
      //      searchView.setVisibility(View.VISIBLE); //Disabled in current RC
   }

   @Override
   public void onRefreshProgress() {
      transactionsView.setVisibility(View.GONE);
      loader.setVisibility(View.VISIBLE);
   }

   @Override
   public void showLoadingFooter(boolean show) {
      post(() -> transactionsView.showLoadingFooter(show));
   }

   @Override
   public void onRefreshError(String error) {

   }

   @Override
   public void showEmpty(boolean isShow) {

   }

   @Override
   public void setTransactionsView(TransactionView transactionsView) {
      this.transactionsView = transactionsView;
   }
}
