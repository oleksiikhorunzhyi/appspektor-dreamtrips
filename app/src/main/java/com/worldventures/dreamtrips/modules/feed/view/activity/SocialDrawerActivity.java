package com.worldventures.dreamtrips.modules.feed.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.utils.delegate.DrawerOpenedEventDelegate;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerView;

import javax.inject.Inject;

import butterknife.InjectView;
import icepick.State;

public abstract class SocialDrawerActivity<P extends ActivityPresenter> extends ActivityWithPresenter<P> {

   @InjectView(R.id.drawer) protected DrawerLayout drawerLayout;
   @InjectView(R.id.toolbar_actionbar) protected Toolbar toolbar;

   @Inject protected RootComponentsProvider rootComponentsProvider;
   @Inject DrawerOpenedEventDelegate drawerOpenedEventDelegate;

   protected NavigationDrawerPresenter navigationDrawerPresenter;

   @State int defaultActionBarContentInset;
   @State boolean toolbarGone;
   @State protected ComponentDescription currentComponent;

   @Override
   protected void afterCreateView(Bundle savedInstanceState) {
      super.afterCreateView(savedInstanceState);
      setupActionBar();
      initNavDrawer();
   }

   @Override
   protected void onResume() {
      super.onResume();
      makeActionBarGone(toolbarGone);
   }

   @Override
   public void onDestroy() {
      navigationDrawerPresenter.detach();
      super.onDestroy();
   }

   protected void setupActionBar() {
      setSupportActionBar(toolbar);
      if (defaultActionBarContentInset == 0) {
         defaultActionBarContentInset = toolbar.getContentInsetStartWithNavigation();
      }
   }

   private void initNavDrawer() {
      setupDrawerLayout();
      navigationDrawerPresenter = new NavigationDrawerPresenter();
      inject(navigationDrawerPresenter);
      navigationDrawerPresenter.attachView(getNavigationDrawer(), rootComponentsProvider.getActiveComponents());
      navigationDrawerPresenter.setOnItemReselected(this::itemReseleted);
      navigationDrawerPresenter.setOnItemSelected(this::itemSelected);
      navigationDrawerPresenter.setOnLogout(this::logout);
   }

   protected abstract NavigationDrawerView getNavigationDrawer();

   protected void setupDrawerLayout() {
      ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
         @Override
         public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            SocialDrawerActivity.this.onDrawerClosed();
         }

         @Override
         public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            SocialDrawerActivity.this.onDrawerOpened();
         }
      };
      drawerLayout.setDrawerListener(drawerToggle);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
      drawerLayout.post(drawerToggle::syncState);

      if (!ViewUtils.isLandscapeOrientation(this)) {
         enableLeftDrawer();
         drawerToggle.setDrawerIndicatorEnabled(true);
      } else {
         disableLeftDrawer();
         drawerToggle.setDrawerIndicatorEnabled(false);
      }
   }

   private void onDrawerClosed() {
      disableRightDrawer();
      invalidateOptionsMenu();
   }

   private void onDrawerOpened() {
      invalidateOptionsMenu();
      SoftInputUtil.hideSoftInputMethod(this);
      drawerOpenedEventDelegate.post(null);
   }


   protected void itemReseleted(ComponentDescription componentDescription) {
      closeLeftDrawer();
   }

   protected void itemSelected(ComponentDescription component) { }

   protected void logout() {
      new MaterialDialog.Builder(this).title(getString(R.string.logout_dialog_title))
            .content(getString(R.string.logout_dialog_message))
            .positiveText(getString(R.string.logout_dialog_positive_btn))
            .negativeText(getString(R.string.logout_dialog_negative_btn))
            .positiveColorRes(R.color.theme_main_darker)
            .negativeColorRes(R.color.theme_main_darker)
            .callback(new MaterialDialog.ButtonCallback() {
               @Override
               public void onPositive(MaterialDialog dialog) {
                  getPresentationModel().logout();
               }
            })
            .show();
   }


   protected void openComponent(ComponentDescription component) {
      Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container_main);
      // check if current
      boolean theSame = currentFragment != null && currentFragment.getClass().equals(component.getFragmentClass());
      if (theSame) return;
      //
      navigationDrawerPresenter.setCurrentComponent(component);
      // check if in stack
      String backStackName = null;
      FragmentManager fm = getSupportFragmentManager();
      for (int entry = 0; entry < fm.getBackStackEntryCount(); entry++) {
         String name = fm.getBackStackEntryAt(entry).getName();
         if (name.equals(component.getKey())) {
            backStackName = name;
            break;
         }
      }
      if (backStackName != null) {
         fm.popBackStack(backStackName, 0);
         return;
      }
      router.moveTo(Route.restoreByKey(component.getKey()), NavigationConfigBuilder.forFragment()
            .fragmentManager(getSupportFragmentManager())
            .containerId(R.id.container_main)
            .backStackEnabled(true)
            .build());
   }


   protected void updateActionBar(ComponentDescription component) {
      setTitle(component.getToolbarTitle());
      setToolbarLogo(component.getToolbarLogo());
   }

   public void makeActionBarGone(boolean hide) {
      this.toolbarGone = hide;
      if (hide) {
         toolbar.setVisibility(View.GONE);
      } else {
         toolbar.setVisibility(View.VISIBLE);
         toolbar.getBackground().setAlpha(255);
      }
   }

   @Override
   public void setTitle(int title) {
      if (title != 0) {
         getSupportActionBar().setTitle(title);
         toolbar.setContentInsetStartWithNavigation(defaultActionBarContentInset);
      } else {
         getSupportActionBar().setTitle("");
      }
   }

   public void setToolbarLogo(int logo) {
      if (logo != 0) {
         getSupportActionBar().setLogo(logo);
         toolbar.setContentInsetStartWithNavigation(0);
      } else {
         getSupportActionBar().setLogo(null);
      }
   }

   @Override
   public void onBackPressed() {
      if (!handleBackPressed()) {
         super.onBackPressed();
      }
   }

   private boolean handleBackPressed() {
      if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
         closeRightDrawer();
         return true;
      } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
         closeLeftDrawer();
         return true;
      }
      return false;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Drawer helpers
   ///////////////////////////////////////////////////////////////////////////

   public void openLeftDrawer() {
      if (!ViewUtils.isLandscapeOrientation(this)) {
         drawerLayout.openDrawer(GravityCompat.START);
      }
   }

   public void closeLeftDrawer() {
      if (!ViewUtils.isLandscapeOrientation(this)) {
         drawerLayout.closeDrawer(GravityCompat.START);
      }
   }

   public void openRightDrawer() {
      drawerLayout.openDrawer(GravityCompat.END);
      enableRightDrawer();
   }

   public void closeRightDrawer() {
      drawerLayout.closeDrawer(GravityCompat.END);
   }

   public void disableLeftDrawer() {
      drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
   }

   public void enableLeftDrawer() {
      drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
   }

   public void disableRightDrawer() {
      drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
   }

   public void enableRightDrawer() {
      drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
   }

   @Override
   public void startActivityForResult(Intent intent, int requestCode) {
      // workaround for Play Services bug
      // https://fabric.io/techery3/android/apps/com.worldventures.dreamtrips/issues/57d833ba0aeb16625b32a442
      // https://github.com/google/gcm/issues/209
      if (intent != null) {
         super.startActivityForResult(intent, requestCode);
      }
   }

   @Override
   protected void onTopLevelBackStackPopped() {
      super.onTopLevelBackStackPopped();
      updateCurrentComponentTitle();
   }

   protected void updateCurrentComponentTitle() {
      currentComponent = rootComponentsProvider.getComponent(getSupportFragmentManager());

      if (rootComponentsProvider.getActiveComponents().contains(currentComponent)) {
         navigationDrawerPresenter.setCurrentComponent(currentComponent);
         updateActionBar(currentComponent);
         makeActionBarGone(currentComponent.skipGeneralToolbar());
      }
   }
}
