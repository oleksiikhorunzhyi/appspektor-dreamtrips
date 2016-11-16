package com.messenger.ui.view.layout;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.messenger.ui.presenter.MessengerPresenter;
import com.worldventures.dreamtrips.core.flow.layout.BaseViewStateLinearLayout;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.core.MessengerConnectionOverlay;

import icepick.Icepick;
import rx.Observable;
import rx.subjects.PublishSubject;

public abstract class MessengerLinearLayout<V extends MessengerScreen, P extends MessengerPresenter<V, ?>> extends BaseViewStateLinearLayout<V, P> implements MessengerScreen {

   private MessengerConnectionOverlay messengerConnectionOverlay;

   private PublishSubject detachStopper = PublishSubject.create();

   private Bundle lastRestoredInstanceState;

   public MessengerLinearLayout(Context context) {
      super(context);
   }

   public MessengerLinearLayout(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      detachStopper.onNext(null);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Connection overlay
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void initDisconnectedOverlay(Observable<ConnectionState> syncStatus) {
      messengerConnectionOverlay = new MessengerConnectionOverlay(getContext(), this);
      messengerConnectionOverlay.getRetryObservable()
            .compose(bindView())
            .subscribe(click -> getPresenter().onDisconnectedOverlayClicked());
      messengerConnectionOverlay.startProcessingState(syncStatus, detachStopper);
   }

   ///////////////////////////////////////////////////////////////////////////
   // Lifecycle and helpers
   ///////////////////////////////////////////////////////////////////////////

   protected <T> Observable.Transformer<T, T> bindView() {
      return input -> input.takeUntil(detachStopper);
   }

   protected <T> Observable.Transformer<T, T> bindViewIoToMainComposer() {
      return input -> input.compose(new IoToMainComposer<>()).compose(bindView());
   }

   protected boolean inflateToolbarMenu(Toolbar toolbar) {
      if (getPresenter().getToolbarMenuRes() <= 0) {
         return false;
      }
      if (toolbar.getMenu() != null) {
         toolbar.getMenu().clear();
      }
      toolbar.inflateMenu(getPresenter().getToolbarMenuRes());
      getPresenter().onToolbarMenuPrepared(toolbar.getMenu());
      toolbar.setOnMenuItemClickListener(getPresenter()::onToolbarMenuItemClick);
      return true;
   }

   @Override
   public Parcelable onSaveInstanceState() {
      return Icepick.saveInstanceState(this, super.onSaveInstanceState());
   }

   @Override
   public void onRestoreInstanceState(Parcelable state) {
      lastRestoredInstanceState = (Bundle) state;
      super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
   }

   public Bundle getLastRestoredInstanceState() {
      return lastRestoredInstanceState;
   }
}
