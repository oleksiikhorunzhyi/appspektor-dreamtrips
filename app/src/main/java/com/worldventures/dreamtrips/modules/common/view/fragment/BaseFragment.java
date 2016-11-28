package com.worldventures.dreamtrips.modules.common.view.fragment;


import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.fragment.InjectingFragment;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.router.Router;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.MonitoringHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.OfflineWarningDelegate;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.ConnectionState;
import com.worldventures.dreamtrips.modules.common.view.connection_overlay.core.SocialConnectionOverlay;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import icepick.Icepick;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public abstract class BaseFragment<PM extends Presenter> extends InjectingFragment implements Presenter.View {

   private final PublishSubject stopper = PublishSubject.create();

   private PM presenter;

   @Inject protected Router router;
   @Inject protected Presenter.TabletAnalytic tabletAnalytic;

   @Inject OfflineWarningDelegate offlineWarningDelegate;
   protected SocialConnectionOverlay connectionOverlay;

   public PM getPresenter() {
      return presenter;
   }

   protected abstract PM createPresenter(Bundle savedInstanceState);

   private List<Boolean> userVisibleHints = new ArrayList<>();

   @Override
   protected ObjectGraph getInitialObjectGraph() {
      return super.getInitialObjectGraph().plus(new BaseFragmentModule(this, this, this));
   }

   ///////////////////////////////////////////////////////////////////////////
   // Lifecycle
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void onCreate(Bundle savedInstanceState) {
      MonitoringHelper.setInteractionName(this);
      super.onCreate(savedInstanceState);
      this.presenter = createPresenter(savedInstanceState);
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
      if (presenter != null) this.presenter.saveInstanceState(outState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      Layout layout = getLayoutFromAnnotation(this.getClass());
      if (layout == null) {
         throw new IllegalArgumentException("ConfigurableFragment should have Layout annotation");
      }
      return inflater.inflate(layout.value(), container, false);
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      if (userVisibleHints.contains(true)) track();
   }

   @Override
   public void onDetach() {
      super.onDetach();
      stopper.onNext(null);
   }

   @Override
   public void setUserVisibleHint(boolean isVisibleToUser) {
      super.setUserVisibleHint(isVisibleToUser);
      userVisibleHints.add(isVisibleToUser);
      if (isVisibleToUser && presenter != null) track();
   }

   /**
    * Recursively scans class hierarchy searching for {@link Layout} annotation defined.
    *
    * @param clazz class to search for annotation
    * @return defined layout if any or <b>null</b>
    */
   @Nullable
   private Layout getLayoutFromAnnotation(Class clazz) {
      if (clazz == null || clazz.equals(Object.class)) return null;
      //
      Layout layout = (Layout) clazz.getAnnotation(Layout.class);
      if (layout != null) {
         return layout;
      } else {
         return getLayoutFromAnnotation(clazz.getSuperclass());
      }
   }

   @Override
   public void onViewCreated(View view, Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      ButterKnife.inject(this, view);
      afterCreateView(view);
      restoreState(savedInstanceState);
      //
      this.presenter.takeView(this);
   }

   protected void restoreState(Bundle savedInstanceState) {
   }

   @Override
   public void onPrepareOptionsMenu(Menu menu) {
      super.onPrepareOptionsMenu(menu);
      if (this.presenter != null && isAdded()) this.presenter.onMenuPrepared();
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
      userVisibleHints.clear();
      super.onDestroyView();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Notif helpers
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void informUser(String message) {
      if (isAdded() && getView() != null) try {
         Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
      } catch (Exception e) {
         // Snackbar initialization can produce NullPointerException on view parent getContext();
         Timber.e(e.getMessage());
      }
   }

   @Override
   public void informUser(int stringId) {
      if (isAdded() && getView() != null) try {
         Snackbar.make(getView(), stringId, Snackbar.LENGTH_SHORT).show();
      } catch (Exception e) {
         // Snackbar initialization can produce NullPointerException on view parent getContext();
         Timber.e(e.getMessage());
      }
   }

   @Override
   public void showOfflineAlert() {
      offlineWarningDelegate.showOfflineWarning(getActivity());
   }

   protected void track() {

   }

   @Override
   public void initConnectionOverlay(Observable<ConnectionState> connectionStateObservable, Observable stopper) {
      connectionOverlay = new SocialConnectionOverlay(getContext(), getView(), getContentLayoutId());
      connectionOverlay.startProcessingState(connectionStateObservable, stopper);
   }

   protected @IdRes int getContentLayoutId() {
      return R.id.content_layout;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Misc helpers
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public boolean isTabletLandscape() {
      return tabletAnalytic.isTabletLandscape();
   }

   @Override
   public boolean isVisibleOnScreen() {
      return ViewUtils.isFullVisibleOnScreen(this);
   }

   public void hideSoftInput(View view) {
      SoftInputUtil.hideSoftInputMethod(view);
   }

   /**
    * Will hide (if possible) soft input based on current fragment's view
    */
   public void tryHideSoftInput() {
      if (getView() != null) SoftInputUtil.hideSoftInputMethod(getView());
   }
}
