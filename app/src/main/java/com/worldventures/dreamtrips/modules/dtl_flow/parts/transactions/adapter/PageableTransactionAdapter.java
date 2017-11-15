package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;

import java.util.ArrayList;
import java.util.List;

public class PageableTransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

   private Context context;
   private AdapterItem loader = AdapterItem.forLoader();
   private List<AdapterItem> items = new ArrayList<>();
   private boolean loaderIsShowing;

   private static final int ITEM = 0;
   private static final int LOADING = 1;

   public PageableTransactionAdapter(Context context) {
      this.context = context;
   }

   public List<TransactionModel> getCurrentItems() {
      return Queryable.from(items).filter(adapterItem -> adapterItem.transactionModel != null)
            .map(adapterItem -> adapterItem.transactionModel).toList();
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
         default:
            break;
      }
      return viewHolder;
   }

   @Override
   public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      switch (getItemViewType(position)) {
         case ITEM:
            final PageableTransactionAdapter.ViewHolder recyclerViewHolder = (PageableTransactionAdapter.ViewHolder) holder;
            recyclerViewHolder.bind(items.get(position).transactionModel);
            break;
         default:
            break;
      }
   }

   @Override
   public int getItemCount() {
      return items.size();
   }

   @Override
   public int getItemViewType(int position) {
      return items.get(position).type;
   }

   public void setTransactions(List<TransactionModel> transactionsList) {
      items.clear();
      for (TransactionModel model : transactionsList) {
         this.items.add(AdapterItem.forTransaction(model));
      }
      if (loaderIsShowing) {
         items.add(loader);
      }
      notifyDataSetChanged();
   }

   public void addTransactions(List<TransactionModel> transactionModels) {
      for (TransactionModel model : transactionModels) {
         items.add(AdapterItem.forTransaction(model));
      }
      notifyDataSetChanged();
   }

   public boolean isEmpty() {
      return getItemCount() == 0;
   }

   public void addLoadingFooter() {
      if (!items.contains(loader)) {
         items.add(loader);
      }
      loaderIsShowing = true;
      notifyDataSetChanged();
   }

   public void removeLoadingFooter() {
      if (items.contains(loader)) {
         items.remove(loader);
      }
      loaderIsShowing = false;
      notifyDataSetChanged();
   }

   public static class AdapterItem {
      int type;
      TransactionModel transactionModel;

      public AdapterItem(int type, TransactionModel transactionModel) {
         this.type = type;
         this.transactionModel = transactionModel;
      }

      static AdapterItem forTransaction(TransactionModel transactionModel) {
         return new AdapterItem(ITEM, transactionModel);
      }

      static AdapterItem forLoader() {
         return new AdapterItem(LOADING, null);
      }
   }

   private static class ViewHolder extends RecyclerView.ViewHolder {
      public TextView merchantName;
      public TextView earnedPoints;
      public ImageView earnedPointsIcon;
      public TextView transactionDate;
      public TextView subtotal;
      public Context context;
      public ImageView statusImageView;

      public ViewHolder(View itemView) {
         super(itemView);
         this.context = itemView.getContext();
         merchantName = itemView.findViewById(R.id.merchant_name);
         earnedPoints = itemView.findViewById(R.id.earned_points);
         earnedPointsIcon = itemView.findViewById(R.id.earned_points_icon);
         transactionDate = itemView.findViewById(R.id.transaction_date);
         subtotal = itemView.findViewById(R.id.subtotal);
         statusImageView = itemView.findViewById(R.id.imageViewStatus);
      }

      public void bind(TransactionModel transactionModel) {
         merchantName.setText(transactionModel.getMerchantName());
         earnedPoints.setText(getEarnedPointText(transactionModel.getEarnedPoints()));
         earnedPointsIcon.setVisibility(View.VISIBLE);
         earnedPointsIcon.setBackgroundResource(R.drawable.dt_points_big_icon);
         if(transactionModel.isTrhstTransaction())
            statusImageView.setImageResource(TransactionModel.ThrstPaymentStatus.SUCCESSFUL.equals(transactionModel.getThrstPaymentStatus()) ? R.drawable.check_succes_pilot : R.drawable.check_error_pilot);
         else
            statusImageView.setImageBitmap(null);
         transactionDate.setText(DateTimeUtils.convertDateToString(transactionModel.getTransactionDate(),
               DateTimeUtils.TRANSACTION_DATE_FORMAT));
         subtotal.setText(context.getString(R.string.dtl_subtotal, transactionModel.getSubTotalAmount()));
      }

      private String getEarnedPointText(int earnedPoints) {
         return String.format(itemView.getContext().getString(R.string.dtl_earned_points_on_item), earnedPoints);
      }

   }

   protected class LoadingVH extends RecyclerView.ViewHolder {
      public LoadingVH(View itemView) {
         super(itemView);
      }
   }

}