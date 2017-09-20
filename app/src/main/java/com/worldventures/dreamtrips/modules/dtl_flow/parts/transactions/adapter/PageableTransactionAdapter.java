package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.adapter.ReviewAdapter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.CSTConverter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class PageableTransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

   private Context context;
   private List<TransactionModel> transactionsList = new ArrayList<>();

   // View Types
   private static final int ITEM = 0;
   private static final int LOADING = 1;

   public PageableTransactionAdapter(Context context) {
      this.context = context;
   }

   public PageableTransactionAdapter(Context context, List<TransactionModel> transactionsList) {
      this.context = context;
      this.transactionsList = transactionsList;
   }

   public List<TransactionModel> getCurrentItems() {
      return transactionsList;
   }

   @Override
   public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      RecyclerView.ViewHolder viewHolder = null;
      LayoutInflater inflater = LayoutInflater.from(parent.getContext());

      switch (viewType) {
         case ITEM:
            View viewItem = inflater.inflate(R.layout.item_transaction, parent, false);
            viewHolder = new PageableTransactionAdapter.ViewHolder(viewItem);
            break;
         case LOADING:
            View viewLoading = inflater.inflate(R.layout.view_dtl_item_loading, parent, false);
            viewHolder = new PageableTransactionAdapter.LoadingVH(viewLoading);
            break;
      }
      return viewHolder;
   }

   @Override
   public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      switch (getItemViewType(position)) {
         case ITEM:
            final PageableTransactionAdapter.ViewHolder recyclerViewHolder = (PageableTransactionAdapter.ViewHolder) holder;
            recyclerViewHolder.bind(position);
            break;
      }
   }

   @Override
   public int getItemCount() {
      return transactionsList.size();
   }

   @Override
   public int getItemViewType(int position) {
      String merchantName = getItem(position).getMerchantName();
      return merchantName == null || merchantName.length() == 0 ? LOADING : ITEM;
   }

   public List<TransactionModel> getTransactionsList() {
      return transactionsList;
   }

   public void setTransactionsList(List<TransactionModel> transactionsList) {
      this.transactionsList = transactionsList;
   }

    /*
        Helpers - Pagination
   _________________________________________________________________________________________________
    */

   public void add(TransactionModel r) {
      transactionsList.add(r);
      notifyItemInserted(transactionsList.size() - 1);
   }

   public void addItems(List<TransactionModel> transactionModels) {
      for (TransactionModel object : transactionModels) {
         add(object);
      }
   }

   public void remove(TransactionModel r) {
      int position = transactionsList.indexOf(r);
      if (position > -1) {
         transactionsList.remove(position);
         notifyItemRemoved(position);
      }
   }

   public void clear() {
      while (getItemCount() > 0) {
         remove(getItem(0));
      }
   }

   public boolean isEmpty() {
      return getItemCount() == 0;
   }

   public void addLoadingFooter() {
      add(new TransactionModel());
   }

   public void removeLoadingFooter() {
      if (transactionsList == null || transactionsList.isEmpty()) return;

      int position = transactionsList.size() - 1;

      if (getItemViewType(position) == ITEM) return;

      TransactionModel object = getItem(position);

      if (object != null) {
         transactionsList.remove(position);
         notifyItemRemoved(position);
      }
   }

   public TransactionModel getItem(int position) {
      return transactionsList.get(position);
   }

   public class ViewHolder extends RecyclerView.ViewHolder {
      public TextView merchantName;
      public TextView earnedPoints;
      public ImageView earnedPointsIcon;
      public TextView transactionDate;


      public ViewHolder(View itemView) {
         super(itemView);
         merchantName = (TextView) itemView.findViewById(R.id.merchant_name);
         earnedPoints = (TextView) itemView.findViewById(R.id.earned_points);
         earnedPointsIcon = (ImageView) itemView.findViewById(R.id.earned_points_icon);
         transactionDate = (TextView) itemView.findViewById(R.id.transaction_date);
      }

      public void bind(int position) {
         merchantName.setText(transactionsList.get(position).getMerchantName());
         earnedPoints.setText(transactionsList.get(position).getEarnedPoints());
         earnedPointsIcon.setVisibility(View.VISIBLE);
         earnedPointsIcon.setBackgroundResource(R.drawable.dt_points_big_icon);
         transactionDate.setText(DateTimeUtils.getStringDateFromStringUTC(transactionsList.get(position).getTransactionDate()));
      }

   }

   protected class LoadingVH extends RecyclerView.ViewHolder {
      public LoadingVH(View itemView) {
         super(itemView);
      }
   }

}

