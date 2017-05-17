package com.worldventures.dreamtrips.core.flow.layout;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.messenger.ui.presenter.ViewStateMvpPresenter;

public abstract class BaseViewStateConstraintLayout<V extends MvpView, P extends ViewStateMvpPresenter<V, ?>> extends MvpConstraintLayout<V, P> {

   private final ViewStateDelegate delegate;

   public BaseViewStateConstraintLayout(Context context) {
      this(context, null);
   }

   public BaseViewStateConstraintLayout(Context context, AttributeSet attrs) {
      super(context, attrs);
      delegate = new ViewStateDelegate(super::getPresenter);
   }

   @Override
   protected void onAttachedToWindow() {
      if (!isInEditMode()) {
         super.onAttachedToWindow();
         onPostAttachToWindowView();
         delegate.onAttachedToWindow();
      }
   }

   protected void onPostAttachToWindowView() {
      delegate.onPostAttachToWindowView();
   }

   @Override
   protected void onDetachedFromWindow() {
      if (!isInEditMode()) {
         super.onDetachedFromWindow();
         delegate.onDetachedFromWindow();
      }
   }

   @Override
   protected void onWindowVisibilityChanged(int visibility) {
      super.onWindowVisibilityChanged(visibility);
      if (!isInEditMode()) {
         delegate.onWindowVisibilityChanged(visibility);
      }
   }

   @Override
   protected Parcelable onSaveInstanceState() {
      return delegate.onSaveInstanceState(super.onSaveInstanceState());
   }

   @Override
   protected void onRestoreInstanceState(Parcelable state) {
      super.onRestoreInstanceState(delegate.onRestoreInstanceState(state));
   }
}
