package com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.transactions.model.TransactionModel;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.utils.CurrencyUtils;

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
         statusImageView = itemView.findViewById(R.id.imageViewStatus);
         subtotal = itemView.findViewById(R.id.subtotal);
      }

      public void bind(TransactionModel transactionModel) {
         merchantName.setText(transactionModel.getMerchantName());
         earnedPoints.setText(getEarnedPointText(transactionModel.getEarnedPoints()));
         earnedPointsIcon.setVisibility(View.VISIBLE);
         earnedPointsIcon.setBackgroundResource(R.drawable.dt_points_big_icon);
         setPaymentStatusIcon(transactionModel);
         transactionDate.setText(DateTimeUtils.convertDateToString(transactionModel.getTransactionDate(),
               DateTimeUtils.TRANSACTION_DATE_FORMAT));
         formatSubtotal(transactionModel);
      }

      private void formatSubtotal(TransactionModel transactionModel) {
         final String subtotalValue = CurrencyUtils.toCurrency(transactionModel.getSubTotalAmount(),
               transactionModel.getCurrenyCode(), transactionModel.getCurrencySymbol());
         final String subtotalCaption = context.getString(R.string.dtl_subtotal, subtotalValue);

         if (transactionModel.getThrstPaymentStatus() == TransactionModel.ThrstPaymentStatus.REFUNDED) {
            final int spanStartPosition = subtotalCaption.indexOf(subtotalValue);
            final int spanEndPosition = subtotalCaption.length();
            final int colorRes = transactionModel.getTotalAmount() < 0D ?
                  R.color.transaction_amount_red_color : R.color.transaction_amount_green_color;
            final int color = ContextCompat.getColor(context, colorRes);

            Spannable colored = new SpannableString(subtotalCaption);
            colored.setSpan(new ForegroundColorSpan(color), spanStartPosition, spanEndPosition,
                  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            subtotal.setText(colored, TextView.BufferType.SPANNABLE);
         } else {
            subtotal.setText(subtotalCaption);
         }
      }

      private void setPaymentStatusIcon(TransactionModel transactionModel) {
         if (transactionModel.isTrhstTransaction()) {
            switch (transactionModel.getThrstPaymentStatus()) {
               case SUCCESSFUL:
               case INITIATED:
                  statusImageView.setImageResource(R.drawable.check_succes_pilot);
                  break;
               case REFUNDED:
                  statusImageView.setImageResource(R.drawable.check_refund_pilot);
                  break;
               default:
                  statusImageView.setImageResource(R.drawable.check_error_pilot);
                  break;
            }
         } else {
            statusImageView.setImageBitmap(null);
         }
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
