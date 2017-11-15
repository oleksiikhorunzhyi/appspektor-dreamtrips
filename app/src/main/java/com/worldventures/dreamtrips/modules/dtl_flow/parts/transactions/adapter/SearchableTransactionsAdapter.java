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

import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import java.util.ArrayList;
import java.util.List;

public class SearchableTransactionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

   private final Context context;
   private List<TransactionModel> transactionsList = new ArrayList<>();
   protected List<TransactionModel> originalList = new ArrayList<>();

   public SearchableTransactionsAdapter(Context context) {
      this.context = context;
   }

   public SearchableTransactionsAdapter(Context context, List<TransactionModel> transactionsList) {
      this.context = context;
      this.transactionsList = transactionsList;
      this.originalList = transactionsList;
   }

   public void setTransactionsList(List<TransactionModel> transactionsList) {
      this.originalList = transactionsList;
      this.transactionsList = transactionsList;
      notifyDataSetChanged();
   }

   public List<TransactionModel> getAllItems() {
      return originalList;
   }

   @Override
   public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
      return new SearchableTransactionsAdapter.ViewHolder(itemView);
   }

   @Override
   public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      final SearchableTransactionsAdapter.ViewHolder recyclerViewHolder = (SearchableTransactionsAdapter.ViewHolder) holder;
      recyclerViewHolder.bind(transactionsList.get(position));
   }

   @Override
   public int getItemCount() {
      return transactionsList == null ? 0 : transactionsList.size();
   }

   public class ViewHolder extends RecyclerView.ViewHolder {
      public TextView merchantName;
      public TextView earnedPoints;
      public ImageView earnedPointsIcon;
      public TextView transactionDate;
      public ImageView statusImageView;

      public ViewHolder(View itemView) {
         super(itemView);
         merchantName = itemView.findViewById(R.id.merchant_name);
         earnedPoints = itemView.findViewById(R.id.earned_points);
         earnedPointsIcon = itemView.findViewById(R.id.earned_points_icon);
         transactionDate = itemView.findViewById(R.id.transaction_date);
         statusImageView = itemView.findViewById(R.id.imageViewStatus);

      }

      public void bind(TransactionModel transactionModel) {
         merchantName.setText(transactionModel.getMerchantName());
         earnedPoints.setText(getEarnedPointText(transactionModel.getEarnedPoints()));
         earnedPointsIcon.setVisibility(View.VISIBLE);
         earnedPointsIcon.setBackgroundResource(R.drawable.dt_points_big_icon);
         statusImageView.setImageResource(TransactionModel.ThrstPaymentStatus.SUCCESSFUL.equals(transactionModel.getThrstPaymentStatus()) ? R.drawable.check_succes_pilot : R.drawable.check_error_pilot);
         transactionDate.setText(DateTimeUtils.convertDateToString(transactionModel.getTransactionDate(),
               DateTimeUtils.TRANSACTION_DATE_FORMAT));
      }

      private String getEarnedPointText(int earnedPoints) {
         return String.format(context.getString(R.string.dtl_earned_points_on_item), earnedPoints);
      }

   }

   @Override
   public Filter getFilter() {
      return new Filter() {
         @SuppressWarnings("unchecked")
         @Override
         protected void publishResults(CharSequence constraint, FilterResults results) {
            transactionsList = (List<TransactionModel>) results.values;
            SearchableTransactionsAdapter.this.notifyDataSetChanged();
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

   public List<TransactionModel> getCurrentItems() {
      return  transactionsList;
   }

   }
