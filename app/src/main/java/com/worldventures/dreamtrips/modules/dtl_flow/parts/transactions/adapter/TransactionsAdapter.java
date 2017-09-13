package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.model.CSTConverter;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable{

   private Context context;
   private List<TransactionModel> transactionsList;
   protected List<TransactionModel> originalList;

   public TransactionsAdapter(Context context, List<TransactionModel> transactionsList){
      this.context = context;
      this.transactionsList = transactionsList;
      this.originalList = transactionsList;
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
      viewHolder.subtotalAmount.setText(transactionsList.get(position).getSubTotalAmount());
      viewHolder.earnedPoints.setText(transactionsList.get(position).getEarnedPoints());

      if(transactionsList.get(position).getRewardStatus()){
         viewHolder.earnedPointsIcon.setVisibility(View.VISIBLE);
         viewHolder.earnedPointsIcon.setBackgroundResource(R.drawable.success);
      } else {
         viewHolder.earnedPointsIcon.setVisibility(View.INVISIBLE);
      }

      try {
         viewHolder.transactionDate.setText(DateTimeUtils.convertDateToUTCString(new Date(transactionsList.get(position).getTransactionDate())));
      } catch (Exception ex){
         ex.printStackTrace();
      }

      try {
         CSTConverter converter = new CSTConverter();
         String convertedTime = converter.getCorrectTimeWrote(context, transactionsList.get(position).getTransactionDate());
         viewHolder.transactionTime.setText(convertedTime);
      }catch (ParseException e){
         e.printStackTrace();
      }

      if(transactionsList.get(position).isTransactionSuccess()){
         viewHolder.transactionSuccess.setBackgroundResource(R.drawable.ic_transaction_ok);
      } else {
         viewHolder.transactionSuccess.setBackgroundResource(R.drawable.ic_transaction_failed);
      }
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

   @Override
   public Filter getFilter() {
      return new Filter() {
         @SuppressWarnings("unchecked")
         @Override
         protected void publishResults(CharSequence constraint, FilterResults results) {
            transactionsList = (List<TransactionModel>) results.values;
            TransactionsAdapter.this.notifyDataSetChanged();
         }

         @Override
         protected FilterResults performFiltering(CharSequence constraint) {
            List<TransactionModel> filteredResults = null;
            if (constraint.length() == 0) {
               filteredResults = originalList;
            } else {
               filteredResults = getFilteredResults(constraint.toString().toLowerCase());
            }

            FilterResults results = new FilterResults();
            results.values = filteredResults;

            return results;
         }
      };
   }

   protected List<TransactionModel> getFilteredResults(String constraint) {
      List<TransactionModel> results = new ArrayList<>();
      for (TransactionModel item : originalList) {
         if (item.getMerchantName().toLowerCase().contains(constraint)) {
            results.add(item);
         }
      }
      return results;
   }


}
