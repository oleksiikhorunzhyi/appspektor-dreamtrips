package com.worldventures.dreamtrips.modules.feed.view.custom;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public abstract class BaseViewHolder<PM extends Presenter> implements Holder, Presenter.View {

   protected PM presenter;

   protected View contentView;

   protected DialogPlus dialog;

   @Override
   public void addHeader(View view) {

   }

   @Override
   public void addFooter(View view) {

   }

   @Override
   public void setBackgroundColor(int i) {

   }

   public void setPresenter(PM presenter) {
      this.presenter = presenter;
   }

   public void setDialog(DialogPlus dialog) {
      this.dialog = dialog;
   }

   @Override
   public void setOnKeyListener(View.OnKeyListener onKeyListener) {

   }

   @Override
   public View getInflatedView() {
      return contentView;
   }

   @Override
   public View getHeader() {
      return null;
   }

   @Override
   public View getFooter() {
      return null;
   }

   @Override
   public void informUser(int stringId) {
      if (contentView != null) Snackbar.make(contentView, contentView.getResources()
            .getString(stringId), Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public void informUser(String message) {
      if (contentView != null) Snackbar.make(contentView, message, Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public void alert(String s) {

   }

   @Override
   public boolean isTabletLandscape() {
      return false;
   }

   @Override
   public boolean isVisibleOnScreen() {
      return dialog != null && dialog.isShowing();
   }
}
