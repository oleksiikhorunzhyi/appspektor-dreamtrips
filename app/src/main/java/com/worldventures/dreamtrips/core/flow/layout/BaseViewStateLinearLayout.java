package com.worldventures.dreamtrips.core.flow.layout;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.hannesdorfmann.mosby.mvp.layout.MvpLinearLayout;
import com.messenger.ui.presenter.ViewStateMvpPresenter;

public abstract class BaseViewStateLinearLayout<V extends MvpView, P extends ViewStateMvpPresenter<V, ?>> extends MvpLinearLayout<V, P> {

   private static final String KEY_SUPER_INSTANCE_STATE = "superstate";

   private Bundle lastInstanceState;

   public BaseViewStateLinearLayout(Context context) {
      super(context);
   }

   public BaseViewStateLinearLayout(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      onPostAttachToWindowView();
      // TODO extract to delegate for reuse in other views
      if (lastInstanceState == null) {
         presenter.onNewViewState();
      } else {
         presenter.onRestoreInstanceState(lastInstanceState);
      }

      presenter.onAttachedToWindow();
   }

   protected void onPostAttachToWindowView() {
   }

   @Override
   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      presenter.onDetachedFromWindow();
   }

   @Override
   protected void onWindowVisibilityChanged(int visibility) {
      super.onWindowVisibilityChanged(visibility);
      presenter.onVisibilityChanged(visibility);
   }

   @Override
   protected Parcelable onSaveInstanceState() {
      // TODO extract to delegate for reuse in other views
      Bundle bundle = new Bundle();
      Parcelable parcelableSuper = super.onSaveInstanceState();
      if (parcelableSuper != null) {
         bundle.putParcelable(KEY_SUPER_INSTANCE_STATE, parcelableSuper);
      }
      getPresenter().onSaveInstanceState(bundle);
      return bundle;
   }

   @Override
   protected void onRestoreInstanceState(Parcelable state) {
      // TODO extract to delegate for reuse in other views
      if (state instanceof Bundle) {
         lastInstanceState = (Bundle) state;
         Parcelable superState = ((Bundle) state).getParcelable(KEY_SUPER_INSTANCE_STATE);
         super.onRestoreInstanceState(superState);
      }
   }
}
