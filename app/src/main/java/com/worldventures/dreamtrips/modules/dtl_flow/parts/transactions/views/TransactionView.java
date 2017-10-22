package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.listener.PaginationScrollListener;
import com.worldventures.dreamtrips.modules.common.listener.RecyclerClickListener;
import com.worldventures.dreamtrips.modules.common.listener.RecyclerTouchListener;
import com.worldventures.dreamtrips.modules.common.listener.ScrollEventListener;
import com.worldventures.dreamtrips.modules.dtl_flow.FlowUtil;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler.MarginDecoration;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transaction_detail.DtlTransactionPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.adapter.SearchableTransactionsAdapter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.adapter.PageableTransactionAdapter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import java.util.List;

import flow.Flow;

public class TransactionView extends LinearLayout {

   private RecyclerView recyclerView;
   private SearchableTransactionsAdapter searchableTransactionsAdapter;
   private PageableTransactionAdapter pageableTransactionAdapter;
   private Context context;
   private boolean isLoading = false;
   private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
   private RecyclerView.OnItemTouchListener onItemTouchListener;
   private RecyclerView.OnScrollListener scrollingListener;
   private ScrollEventListener scrollEventListener;

   public TransactionView(Context context) {
      this(context, null);
      this.context = context;
   }

   public TransactionView(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      this.context = context;
      init();
   }

   private void init() {
      final View v = LayoutInflater.from(getContext()).inflate(R.layout.activity_transaction_list, this, true);
      recyclerView = (RecyclerView) v.findViewById(R.id.recycler_adapter);
      initRecycler();
      initAdapters();
   }

   public void addItems(List<TransactionModel> transactions) {
      pageableTransactionAdapter.addTransactions(transactions);
      isLoading = false;
   }

   public void setNotLoading() {
      isLoading = false;
   }

   public void setAllTransactionsList(List<TransactionModel> transactions){
      setPageableAdapter();
      pageableTransactionAdapter.setTransactions(transactions);
   }

   public void setSearchableAdapter(){
      if(recyclerView.getAdapter()!=searchableTransactionsAdapter) {
         recyclerView.setAdapter(searchableTransactionsAdapter);
         searchableTransactionsAdapter.setTransactionsList(pageableTransactionAdapter.getCurrentItems());
      }
   }

   public void setPageableAdapter(){
      if(recyclerView.getAdapter() != pageableTransactionAdapter) recyclerView.setAdapter(pageableTransactionAdapter);
   }

   public void filterByMerchantName(String searchString){
      searchableTransactionsAdapter.getFilter().filter(searchString);
   }

   private void initRecycler() {
      recyclerView.setLayoutManager(linearLayoutManager);
      recyclerView.addItemDecoration(new MarginDecoration(getContext()));
      recyclerView.setHasFixedSize(false);

      onItemTouchListener = new RecyclerTouchListener(getContext(), recyclerView,
            new RecyclerClickListener() {
               @Override
               public void onClick(View view, int position) {
                  TransactionModel transaction;
                  if(recyclerView.getAdapter() instanceof PageableTransactionAdapter)
                     transaction = ((PageableTransactionAdapter) recyclerView.getAdapter()).getCurrentItems().get(position);
                  else
                     transaction = ((SearchableTransactionsAdapter) recyclerView.getAdapter()).getCurrentItems().get(position);

                  DtlTransactionPath path = new DtlTransactionPath(FlowUtil.currentMaster(getContext()), transaction);
                  Flow.get(getContext()).set(path);
               }

               @Override
               public void onLongClick(View view, int position) {
               }
            });
      recyclerView.addOnItemTouchListener(onItemTouchListener);

      scrollingListener = new PaginationScrollListener(linearLayoutManager) {
         @Override
         protected void loadMoreItems() {
            isLoading = true;
            getMoreTransactions();
         }

         @Override
         public boolean isLoading() {
            return isLoading;
         }
      };

      recyclerView.addOnScrollListener(scrollingListener);
   }

   private void initAdapters() {
      searchableTransactionsAdapter = new SearchableTransactionsAdapter(context);
      pageableTransactionAdapter = new PageableTransactionAdapter(getContext());
      recyclerView.setAdapter(pageableTransactionAdapter);
   }

   public void showLoadingFooter(boolean show) {
      if (show)
         pageableTransactionAdapter.addLoadingFooter();
      else
         pageableTransactionAdapter.removeLoadingFooter();
   }

   private void getMoreTransactions() {
      if (scrollEventListener != null && recyclerView.getAdapter() != searchableTransactionsAdapter) {
         scrollEventListener.onScrollBottomReached(0);
      }
   }

   public void setScrollEventListener(ScrollEventListener scrollEventListener) {
      this.scrollEventListener = scrollEventListener;
   }

}
