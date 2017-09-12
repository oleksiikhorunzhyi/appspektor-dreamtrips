package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

   private List<TransactionModel> transactionsList;

   public TransactionsAdapter(List<TransactionModel> transactionsList){
      this.transactionsList = transactionsList;
   }

   @Override
   public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
      return new TransactionsAdapter.ViewHolder(itemView);
   }

   @Override
   public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      ViewHolder viewHolder = (ViewHolder) holder;
      viewHolder.merchantName.setText(transactionsList.get(position).getMerchantName());
      viewHolder.subtotalAmount.setText(transactionsList.get(position).getSubTotalAmount());
      viewHolder.earnedPoints.setText(transactionsList.get(position).getEarnedPoints());
      viewHolder.transactionDate.setText(transactionsList.get(position).getTransactionDate());
      viewHolder.transactionTime.setText(transactionsList.get(position).getTransactionTime());

      if(transactionsList.get(position).isTransactionSuccess()){
         viewHolder.transactionSuccess.setBackgroundResource(R.drawable.ic_other_travel);
      } else {
         viewHolder.transactionSuccess.setBackgroundResource(R.drawable.__leak_canary_icon);
      }
   }

   @Override
   public int getItemCount() {
      return transactionsList.size();
   }

   public class ViewHolder extends RecyclerView.ViewHolder {
      public TextView merchantName;
      public TextView subtotalAmount;
      public TextView earnedPoints;
      public TextView transactionDate;
      public TextView transactionTime;
      public ImageView transactionSuccess;

      public ViewHolder(View itemView) {
         super(itemView);
         merchantName = (TextView) itemView.findViewById(R.id.merchant_name);
         subtotalAmount = (TextView) itemView.findViewById(R.id.subtotal_amount);
         earnedPoints = (TextView) itemView.findViewById(R.id.earned_points);
         transactionDate = (TextView) itemView.findViewById(R.id.transaction_date);
         transactionTime = (TextView) itemView.findViewById(R.id.transaction_time);
         transactionSuccess = (ImageView) itemView.findViewById(R.id.transaction_status_icon);
      }

   }

}
