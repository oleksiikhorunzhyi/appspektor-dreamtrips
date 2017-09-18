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
      View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
      return new PageableTransactionAdapter.ViewHolder(itemView);
   }

   @Override
   public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      drawView(holder, position);
   }

   @Override
   public int getItemCount() {
      return transactionsList.size();
   }

   public List<TransactionModel> getTransactionsList() {
      return transactionsList;
   }

   public void setTransactionsList(List<TransactionModel> transactionsList) {
      this.transactionsList = transactionsList;
   }

   private void drawView(RecyclerView.ViewHolder holder, int position) {
      ViewHolder viewHolder = (ViewHolder) holder;
      viewHolder.merchantName.setText(transactionsList.get(position).getMerchantName());
      viewHolder.subtotalAmount.setText(transactionsList.get(position).getSubTotalAmount());
      viewHolder.earnedPoints.setText(transactionsList.get(position).getEarnedPoints());

      if (transactionsList.get(position).getRewardStatus()) {
         viewHolder.earnedPointsIcon.setVisibility(View.VISIBLE);
         viewHolder.earnedPointsIcon.setBackgroundResource(R.drawable.success);
      } else {
         viewHolder.earnedPointsIcon.setVisibility(View.INVISIBLE);
      }

      viewHolder.transactionDate.setText(DateTimeUtils.getStringDateFromStringUTC(transactionsList.get(position)
            .getTransactionDate()));

      try {
         CSTConverter converter = new CSTConverter();
         String convertedTime = converter.getCorrectTimeWrote(context, transactionsList.get(position)
               .getTransactionDate());
         viewHolder.transactionTime.setText(convertedTime);
      } catch (ParseException e) {
         e.printStackTrace();
      }

      if (transactionsList.get(position).isTransactionSuccess()) {
         viewHolder.transactionSuccess.setBackgroundResource(R.drawable.ic_transaction_ok);
      } else {
         viewHolder.transactionSuccess.setBackgroundResource(R.drawable.ic_transaction_failed);
      }
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
      public TextView subtotalAmount;
      public TextView earnedPoints;
      public ImageView earnedPointsIcon;
      public TextView transactionDate;
      public TextView transactionTime;
      public ImageView transactionSuccess;

      public ViewHolder(View itemView) {
         super(itemView);
         merchantName = (TextView) itemView.findViewById(R.id.merchant_name);
         subtotalAmount = (TextView) itemView.findViewById(R.id.subtotal_amount);
         earnedPoints = (TextView) itemView.findViewById(R.id.earned_points);
         earnedPointsIcon = (ImageView) itemView.findViewById(R.id.earned_points_icon);
         transactionDate = (TextView) itemView.findViewById(R.id.transaction_date);
         transactionTime = (TextView) itemView.findViewById(R.id.transaction_time);
         transactionSuccess = (ImageView) itemView.findViewById(R.id.transaction_status_icon);
      }

   }

}

