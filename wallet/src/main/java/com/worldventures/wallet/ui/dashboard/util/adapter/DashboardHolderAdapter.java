package com.worldventures.wallet.ui.dashboard.util.adapter;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.util.DiffUtil;

import com.worldventures.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.wallet.ui.common.adapter.HolderTypeFactory;
import com.worldventures.wallet.ui.common.adapter.MultiHolderAdapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardHolderAdapter<ITEM extends BaseViewModel> extends MultiHolderAdapter<ITEM> {

   private final ExecutorService exService = Executors.newSingleThreadExecutor();
   private final Handler handler = new Handler(Looper.getMainLooper());

   public DashboardHolderAdapter(List<ITEM> items, HolderTypeFactory holderTypeFactory) {
      super(items, holderTypeFactory);
   }

   public void swapList(List<ITEM> newList) {
      exService.execute(() -> {
         final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CardDiffCallBack<>(items, newList));
         handler.post(() -> {
            diffResult.dispatchUpdatesTo(this);
            items = newList;
         });
      });
   }
}
