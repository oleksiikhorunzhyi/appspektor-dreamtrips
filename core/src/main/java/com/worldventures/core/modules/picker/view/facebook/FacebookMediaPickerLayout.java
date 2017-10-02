package com.worldventures.core.modules.picker.view.facebook;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.worldventures.core.modules.picker.viewmodel.FacebookMediaPickerViewModel;
import com.worldventures.core.modules.picker.presenter.facebook.FacebookMediaPickerPresenter;
import com.worldventures.core.modules.picker.view.base.BaseMediaPickerLayout;


public abstract class FacebookMediaPickerLayout<P extends FacebookMediaPickerPresenter, M extends FacebookMediaPickerViewModel> extends BaseMediaPickerLayout<P, M> {

   private int previousTotal;
   private boolean loading;

   public FacebookMediaPickerLayout(@NonNull Context context) {
      super(context);
   }

   public FacebookMediaPickerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
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
