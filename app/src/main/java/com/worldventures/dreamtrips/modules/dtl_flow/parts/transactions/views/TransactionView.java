package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.views;

import android.app.ProgressDialog;
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
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler.MarginDecoration;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.adapter.SearchableTransactionsAdapter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.adapter.PageableTransactionAdapter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import java.util.List;

public class TransactionView extends LinearLayout {

   private RecyclerView recyclerView;
   private SearchableTransactionsAdapter searchableTransactionsAdapter;
   private PageableTransactionAdapter pageableTransactionAdapter;
   private Context context;
   private boolean isLoading = false;
   private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
   private RecyclerView.OnItemTouchListener onItemTouchListener;
   private RecyclerView.OnScrollListener scrollingListener;

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
      resetViewData();
   }

   public void loadData(List<TransactionModel> transactions) {
      loadPage(transactions);
   }

   private void loadPage(List<TransactionModel> transactions) {
      pageableTransactionAdapter.addItems(transactions);
      isLoading = false;
   }

   public void resetViewData() {
      pageableTransactionAdapter = new PageableTransactionAdapter(getContext());
      recyclerView.setAdapter(pageableTransactionAdapter);
   }

   public void setAllTransactionsList(List<TransactionModel> transactions){
      searchableTransactionsAdapter = new SearchableTransactionsAdapter(context, transactions);
      setSearchableAdapter();
   }

   public void setSearchableAdapter(){
      if(recyclerView.getAdapter()!=searchableTransactionsAdapter) recyclerView.setAdapter(searchableTransactionsAdapter);
   }

   public void setPageableAdapter(){
      if(recyclerView.getAdapter()!=pageableTransactionAdapter) recyclerView.setAdapter(pageableTransactionAdapter);
   }

   public void filterByMerchantName(String searchString){
      searchableTransactionsAdapter.getFilter().filter(searchString);
   }

   public void clearSearch(){
      if(searchableTransactionsAdapter!=null && !searchableTransactionsAdapter.getAllItems().isEmpty()){
            //add all results on pageable adapter
            pageableTransactionAdapter.setTransactionsList(searchableTransactionsAdapter.getAllItems());
      }
      setPageableAdapter();
   }

   private void setActiveScrollListener(boolean active){

   }

   private void initRecycler() {
      recyclerView.setLayoutManager(linearLayoutManager);
      recyclerView.addItemDecoration(new MarginDecoration(getContext()));
      recyclerView.setHasFixedSize(false);

      onItemTouchListener = new RecyclerTouchListener(getContext(), recyclerView,
            new RecyclerClickListener() {
               @Override
               public void onClick(View view, int position) {
               }

               @Override
               public void onLongClick(View view, int position) {
               }
            });
      recyclerView.addOnItemTouchListener(onItemTouchListener);

      scrollingListener = new PaginationScrollListener(linearLayoutManager) {
         @Override
         protected void loadMoreItems() {
//            showLoadingFooter(true);
            isLoading = true;
//            getMoreReviewItems();
         }

         @Override
         public boolean isLoading() {
            return isLoading;
         }
      };

      recyclerView.addOnScrollListener(scrollingListener);

   }

   public boolean hasAllItems(){
      return searchableTransactionsAdapter==null? false : !searchableTransactionsAdapter.getAllItems().isEmpty();
   }

   public boolean hasReviews(){
      return !pageableTransactionAdapter.getCurrentItems().isEmpty();
   }

}
