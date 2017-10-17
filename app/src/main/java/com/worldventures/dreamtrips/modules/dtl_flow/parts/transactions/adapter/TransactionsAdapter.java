package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.util.DtlDateTimeUtils;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

   private Context context;
   private List<TransactionModel> transactionsList;

   public TransactionsAdapter(Context context, List<TransactionModel> transactionsList){
      this.context = context;
      this.transactionsList = transactionsList;
   }

   @Override
   public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
      return new TransactionsAdapter.ViewHolder(itemView);
   }

   @Override
   public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      drawView(holder, position);
   }

   @Override
   public int getItemCount() {
      return transactionsList.size();
   }

   private void drawView(RecyclerView.ViewHolder holder, int position){
      ViewHolder viewHolder = (ViewHolder) holder;
      viewHolder.merchantName.setText(transactionsList.get(position).getMerchantName());
      viewHolder.earnedPoints.setText(getEarnedText(transactionsList.get(position).getEarnedPoints()));
      viewHolder.earnedPointsIcon.setVisibility(View.VISIBLE);
      viewHolder.earnedPointsIcon.setBackgroundResource(R.drawable.dt_points_big_icon);
      viewHolder.transactionDate.setText(DtlDateTimeUtils.getStringDateFromStringUTC(transactionsList.get(position).getTransactionDate()));
   }

   private String getEarnedText(String earnedPoints) {
      if (earnedPoints == null) return "";

      return "+" + earnedPoints + "pt";
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

   }

}
