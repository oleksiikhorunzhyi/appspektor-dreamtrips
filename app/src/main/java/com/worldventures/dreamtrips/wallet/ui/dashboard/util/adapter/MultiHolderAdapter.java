package com.worldventures.dreamtrips.wallet.ui.dashboard.util.adapter;


import android.os.Handler;
import android.os.Looper;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiHolderAdapter<T extends BaseViewModel> extends RecyclerView.Adapter<BaseHolder> {

   private List<T> items;
   private HolderTypeFactory factory;
   private final ExecutorService exService = Executors.newSingleThreadExecutor();
   private Handler handler = new Handler(Looper.getMainLooper());

   public MultiHolderAdapter(List<T> items) {
      this.items = items;
      this.factory = new HolderFactoryImpl();
   }

   @Override
   public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return this.factory.holder(parent, viewType);
   }

   @Override
   public void onBindViewHolder(BaseHolder holder, int position) {
      holder.setData(items.get(position));
   }

   @Override
   public int getItemCount() {
      return items.size();
   }

   @Override
   public int getItemViewType(int position) {
      return items.get(position).type(this.factory);
   }

   public void swapList(List<T> newList) {
      exService.execute(() -> {
         final CardDiffCallBack diffCallBack = new CardDiffCallBack(newList, items);
         final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallBack);
         handler.post(() -> {
            diffResult.dispatchUpdatesTo(MultiHolderAdapter.this);
            items = newList;
         });
      });

   }

}
