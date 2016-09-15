package com.worldventures.dreamtrips.core.flow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.badoo.mobile.util.WeakHandler;
import com.google.gson.Gson;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.flow.path.AttributedPath;
import com.worldventures.dreamtrips.core.flow.path.FullScreenPath;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;
import com.worldventures.dreamtrips.core.flow.util.FlowActivityHelper;
import com.worldventures.dreamtrips.core.flow.util.GsonParceler;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerViewImpl;

import javax.inject.Inject;

import butterknife.InjectView;
import flow.Flow;
import flow.History;
import flow.path.Path;
import flow.path.PathContainerView;

public abstract class FlowActivity<PM extends ActivityPresenter> extends ActivityWithPresenter<PM> implements Flow.Dispatcher {

   @InjectView(R.id.drawer) protected DrawerLayout drawerLayout;
   @InjectView(R.id.drawer_layout) protected NavigationDrawerViewImpl navDrawer;
   @InjectView(R.id.root_container) protected PathContainerView container;

   @Inject protected BackStackDelegate backStackDelegate;
   @Inject protected RootComponentsProvider rootComponentsProvider;
   @Inject protected Gson gson;
   @Inject protected NavigationDrawerPresenter navigationDrawerPresenter;
   @Inject protected ActivityRouter activityRouter;

   private FlowActivityHelper flowActivityHelper;
   private WeakHandler weakHandler = new WeakHandler();

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      //
      initNavDrawer();
      initFlow();
      //
      navigationDrawerPresenter.setCurrentComponent(getCurrentComponent());
   }

   @Override
   protected void onPostCreate(Bundle savedInstanceState) {
      super.onPostCreate(savedInstanceState);
      flowActivityHelper.onCreate(savedInstanceState);
   }

   @Override
   protected void onResume() {
      super.onResume();
      flowActivityHelper.onResume();
   }

   @Override
   protected void onPause() {
      super.onPause();
      flowActivityHelper.onPause();
   }

   @Override
   protected void onNewIntent(Intent intent) {
      super.onNewIntent(intent);
      flowActivityHelper.onNewIntent(intent);
      initFlow();
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      flowActivityHelper.onSaveState(outState, (View) container);
   }

   @Override
   public void onDestroy() {
      flowActivityHelper = null;
      navigationDrawerPresenter.detach();
      super.onDestroy();
   }

   @Override
   public void onBackPressed() {
      if (backStackDelegate.handleBackPressed()) return;
      if (flowActivityHelper.handleBack()) return;
      super.onBackPressed();
   }

   @Override
   public Object onRetainCustomNonConfigurationInstance() {
      return flowActivityHelper.provideNonConfigurationInstance();
   }

   @Override
   public Object getSystemService(@NonNull String name) {
      Object service = null;
      if (flowActivityHelper != null) service = flowActivityHelper.getSystemService(name);
      if (service == null) service = super.getSystemService(name);
      return service;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Navigation drawer
   ///////////////////////////////////////////////////////////////////////////

   private void initNavDrawer() {
      navigationDrawerPresenter.attachView(drawerLayout, navDrawer, rootComponentsProvider.getActiveComponents());
      navigationDrawerPresenter.setOnItemReselected(this::itemReseleted);
      navigationDrawerPresenter.setOnItemSelected(this::itemSelected);
      navigationDrawerPresenter.setOnLogout(this::logout);
   }

   private void itemSelected(ComponentDescription component) {
      activityRouter.openMainWithComponent(component.getKey());
   }

   private void itemReseleted(ComponentDescription route) {
      if (!ViewUtils.isLandscapeOrientation(this)) {
         drawerLayout.closeDrawer(GravityCompat.START);
      }
   }

   public void openLeftDrawer() {
      if (!ViewUtils.isLandscapeOrientation(this)) {
         SoftInputUtil.hideSoftInputMethod(this);
         drawerLayout.openDrawer(GravityCompat.START);
      }
   }

   public void openRightDrawer() {
      SoftInputUtil.hideSoftInputMethod(this);
      drawerLayout.openDrawer(GravityCompat.END);
   }

   private void logout() {
      new MaterialDialog.Builder(this).title(getString(R.string.logout_dialog_title))
            .content(getString(R.string.logout_dialog_message))
            .positiveText(getString(R.string.logout_dialog_positive_btn))
            .negativeText(getString(R.string.logout_dialog_negative_btn))
            .positiveColorRes(R.color.theme_main_darker)
            .negativeColorRes(R.color.theme_main_darker)
            .callback(new MaterialDialog.ButtonCallback() {
               @Override
               public void onPositive(MaterialDialog dialog) {
                  TrackingHelper.logout();
                  getPresentationModel().logout();
               }
            })
            .show();
   }

   protected abstract ComponentDescription getCurrentComponent();

   ///////////////////////////////////////////////////////////////////////////
   // Flow
   ///////////////////////////////////////////////////////////////////////////

   @Override
   public void dispatch(Flow.Traversal traversal, Flow.TraversalCallback callback) {
      SoftInputUtil.hideSoftInputMethod(this);
      //
      Path path = traversal.destination.top();
      setNavigation(path);
      //
      if (traversal.origin.top() instanceof AttributedPath) {
         ((AttributedPath) traversal.origin.top()).onPreDispatch(this);
      }
      //
      weakHandler.post(() -> {
         doOnDispatch(traversal);
         container.dispatch(traversal, callback);
      });
   }

   private void initFlow() {
      // Init flow
      History defaultHistory = provideDefaultHistory();
      if (Flow.get(this) == null) {
         flowActivityHelper = new FlowActivityHelper(this, this, defaultHistory, new GsonParceler(gson));
      } else {
         Flow.get(this).setHistory(defaultHistory, Flow.Direction.REPLACE);
      }
   }

   protected void doOnDispatch(Flow.Traversal traversal) {
   }

   void setNavigation(Path path) {
      boolean enabled = false;
      if (path instanceof AttributedPath) {
         PathAttrs attrs = ((AttributedPath) path).getAttrs();
         enabled = attrs.isDrawerEnabled();
      }
      drawerLayout.setDrawerLockMode(enabled ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
      if (ViewUtils.isLandscapeOrientation(this)) {
         if (path instanceof FullScreenPath && ((FullScreenPath) path).shouldHideDrawer())
            navDrawer.setVisibility(View.GONE);
         else navDrawer.setVisibility(View.VISIBLE);
      }
   }

   protected abstract History provideDefaultHistory();
}
