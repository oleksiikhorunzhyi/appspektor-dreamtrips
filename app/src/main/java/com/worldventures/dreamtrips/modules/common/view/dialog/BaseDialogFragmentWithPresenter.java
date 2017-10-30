package com.worldventures.dreamtrips.modules.common.view.dialog;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;

import butterknife.ButterKnife;
import icepick.Icepick;
import rx.Observable;

public abstract class BaseDialogFragmentWithPresenter<T extends Presenter> extends BaseDialogFragment implements Presenter.View {

   protected T presenter;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.presenter = createPresenter();
      if (this.presenter == null) {
         throw new IllegalArgumentException("Presenter can't be null");
      }
      inject(this.presenter);
      this.presenter.onInjected();
      Icepick.restoreInstanceState(this, savedInstanceState);
      this.presenter.restoreInstanceState(savedInstanceState);
      this.presenter.onCreate(savedInstanceState);
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      Icepick.saveInstanceState(this, outState);
      if (presenter != null) {
         this.presenter.saveInstanceState(outState);
      }
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      Layout layout = this.getClass().getAnnotation(Layout.class);
      if (layout == null) {
         throw new IllegalArgumentException("ConfigurableFragment should have Layout annotation");
      }
      return inflater.inflate(layout.value(), container, false);
   }

   @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      ButterKnife.inject(this, view);
      afterCreateView(view);

      this.presenter.takeView(this);
      this.presenter.onViewTaken();
   }

   @Override
   public void onStart() {
      super.onStart();
      presenter.onStart();
   }

   @Override
   public void onResume() {
      super.onResume();
      presenter.onResume();
   }

   @Override
   public void onPause() {
      super.onPause();
      presenter.onPause();
   }

   @Override
   public void onStop() {
      presenter.onStop();
      super.onStop();
   }

   @Override
   public void onDestroyView() {
      presenter.dropView();
      ButterKnife.reset(this);
      super.onDestroyView();
   }

   protected abstract T createPresenter();

   ///////////////////////////////////////////////////////////////////////////
   // Presenter callback methods
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void informUser(int stringId) {
      if (isAdded() && getView() != null) {
         Snackbar.make(getView(), stringId, Snackbar.LENGTH_SHORT).show();
      }
   }

   @Override
   public void informUser(String string) {
      if (isAdded() && getView() != null) {
         Snackbar.make(getView(), string, Snackbar.LENGTH_SHORT).show();
      }
   }

   @Override
   public void showOfflineAlert() {
      //do nothing
   }

   @Override
   public void initConnectionOverlay(Observable<ConnectionState> connectionStateObservable, Observable<Void> stopper) {
      //do nothing
   }

   @Override
   public boolean isTabletLandscape() {
      return ViewUtils.isTablet(getActivity()) && ViewUtils.isLandscapeOrientation(getActivity());
   }

   @Override
   public boolean isVisibleOnScreen() {
      return ViewUtils.isFullVisibleOnScreen(this);
   }
}
