package com.worldventures.dreamtrips.core.flow.layout;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.messenger.ui.presenter.ViewStateMvpPresenter;

class ViewStateDelegate {

   private static final String KEY_SUPER_INSTANCE_STATE = "superstate";

   private final PresenterProvider provider;

   private Bundle lastInstanceState;

   ViewStateDelegate(PresenterProvider presenterProvider) {
      this.provider = presenterProvider;
   }

   void onAttachedToWindow() {
      if (lastInstanceState == null) {
         provider.providePresenter().onNewViewState();
      } else {
         provider.providePresenter().onRestoreInstanceState(lastInstanceState);
      }
      provider.providePresenter().onAttachedToWindow();
   }

   void onPostAttachToWindowView() {

   }

   void onDetachedFromWindow() {
      provider.providePresenter().onDetachedFromWindow();
   }

   void onWindowVisibilityChanged(int visibility) {
      provider.providePresenter().onVisibilityChanged(visibility);
   }

   Parcelable onSaveInstanceState(Parcelable parcelable) {
      Bundle bundle = new Bundle();
      if (parcelable != null) {
         bundle.putParcelable(KEY_SUPER_INSTANCE_STATE, parcelable);
      }
      provider.providePresenter().onSaveInstanceState(bundle);
      return bundle;
   }

   @Nullable
   Parcelable onRestoreInstanceState(Parcelable state) {
      if (state instanceof Bundle) {
         lastInstanceState = (Bundle) state;
         return ((Bundle) state).getParcelable(KEY_SUPER_INSTANCE_STATE);
      }
      return null;
   }

   interface PresenterProvider {
      ViewStateMvpPresenter providePresenter();
   }
}
