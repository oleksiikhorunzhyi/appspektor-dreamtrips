package com.worldventures.dreamtrips.wallet.ui.common.picker.facebook;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.wallet.ui.common.picker.base.BaseWalletPickerLayout;

public abstract class WalletPickerFacebookLayout<P extends WalletPickerFacebookPresenter, M extends WalletFacebookPickerModel> extends BaseWalletPickerLayout<P, M> {

   private int previousTotal;
   private boolean loading;

   public WalletPickerFacebookLayout(@NonNull Context context) {
      super(context);
   }

   public WalletPickerFacebookLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void initView() {
      super.initView();
      getPickerRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
         @Override
         public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int itemCount = getLayoutManager().getItemCount();
            int lastVisibleItemPosition = getLayoutManager().findLastVisibleItemPosition();
            scrolled(itemCount, lastVisibleItemPosition);
         }
      });
   }

   public void scrolled(int totalItemCount, int lastVisible) {
      if (totalItemCount > previousTotal) {
         loading = false;
         previousTotal = totalItemCount;
      }
      if (!loading && lastVisible == totalItemCount - 1) {
         getPresenter().loadMore();
         loading = true;
      }
   }

   @Override
   public void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      this.loading = false;
      this.previousTotal = 0;
   }
}
