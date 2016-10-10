package com.worldventures.dreamtrips.modules.common.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.innahema.collections.query.queriables.Queryable;
import com.messenger.delegate.CropImageDelegate;
import com.messenger.di.MessengerActivityModule;
import com.messenger.ui.activity.MessengerActivity;
import com.techery.spares.annotations.Layout;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.MenuPressedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.LifecycleEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.MainActivityPresenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.util.DrawerListener;
import com.worldventures.dreamtrips.modules.dtl_flow.DtlActivity;
import com.worldventures.dreamtrips.modules.dtl_flow.di.DtlActivityModule;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerViewImpl;
import com.worldventures.dreamtrips.wallet.di.WalletActivityModule;
import com.worldventures.dreamtrips.wallet.ui.WalletActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import icepick.State;
import rx.schedulers.Schedulers;

@Layout(R.layout.activity_main)
public class MainActivity extends ActivityWithPresenter<MainActivityPresenter> implements MainActivityPresenter.View {

   public static final String COMPONENT_KEY = "MainActivity$ComponentKey";
   public static final String FROM_ACTIVITY_KEY = "MainActivity$FromActivityKey";

   @InjectView(R.id.toolbar_actionbar) protected Toolbar toolbar;
   @InjectView(R.id.drawer) protected DrawerLayout drawerLayout;
   @InjectView(R.id.drawer_layout) protected NavigationDrawerViewImpl navDrawer;

   private ActionBarDrawerToggle mDrawerToggle;
   private List<DrawerListener> rightDrawerListeners = new ArrayList<>();

   @Inject RootComponentsProvider rootComponentsProvider;
   @Inject CropImageDelegate cropImageDelegate;

   @State ComponentDescription currentComponent;
   @State boolean toolbarGone;
   @State int defaultActionBarContentInset;

   private NavigationDrawerPresenter navigationDrawerPresenter;

   @Override
   protected MainActivityPresenter createPresentationModel(Bundle savedInstanceState) {
      return new MainActivityPresenter();
   }

   @Override
   protected void onResume() {
      super.onResume();
      makeActionBarGone(toolbarGone);
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      analyticsInteractor.analyticsActionPipe()
            .send(new LifecycleEvent(LifecycleEvent.ACTION_ONSAVESTATE, outState), Schedulers.immediate());
   }

   @Override
   protected void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      analyticsInteractor.analyticsActionPipe()
            .send(new LifecycleEvent(LifecycleEvent.ACTION_ONRESTORESTATE, savedInstanceState), Schedulers.immediate());
   }

   @Override
   protected void afterCreateView(Bundle savedInstanceState) {
      super.afterCreateView(savedInstanceState);
      //
      String keyComponent = null;
      Class<? extends Activity> fromActivityClazz = this.getClass();
      if (getIntent().getExtras() != null) {
         Bundle bundle = getIntent().getBundleExtra(ActivityRouter.EXTRA_BUNDLE);
         keyComponent = bundle.getString(COMPONENT_KEY);
         fromActivityClazz = (Class<? extends Activity>) bundle.getSerializable(FROM_ACTIVITY_KEY);
         if(fromActivityClazz == null) fromActivityClazz = this.getClass();
      }
      //
      setupActionBar();
      setUpBurger();
      setUpMenu();
      //
      BaseFragment currentFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.container_main);
      //
      if (currentComponent == null && currentFragment != null) {
         currentComponent = rootComponentsProvider.getComponentByFragment(currentFragment.getClass());
      }
      if (currentComponent == null && !TextUtils.isEmpty(keyComponent)) {
         currentComponent = rootComponentsProvider.getComponentByKey(keyComponent);
      }
      if (currentComponent == null) {
         currentComponent = rootComponentsProvider.getActiveComponents().get(0);
      }
      //
      initNavDrawer();
      //
      if (currentFragment == null) {
         itemSelected(currentComponent, fromActivityClazz);
      } else {
         initActionBar(currentComponent);
         navigationDrawerPresenter.setCurrentComponent(currentComponent);
      }
   }

   @Override
   public void onDestroy() {
      navigationDrawerPresenter.detach();
      super.onDestroy();
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
   public void makeActionBarGone(boolean hide) {
      this.toolbarGone = hide;
      if (hide) {
         toolbar.setVisibility(View.GONE);
      } else {
         toolbar.setVisibility(View.VISIBLE);
         toolbar.getBackground().setAlpha(255);
      }
   }

   private void setupActionBar() {
      setSupportActionBar(toolbar);
      if (defaultActionBarContentInset == 0) {
         defaultActionBarContentInset = toolbar.getContentInsetStartWithNavigation();
      }
   }

   private void setUpBurger() {
      mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
         @Override
         public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            disableRightDrawer();
            invalidateOptionsMenu();
         }

         @Override
         public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            invalidateOptionsMenu();
            SoftInputUtil.hideSoftInputMethod(MainActivity.this);
            eventBus.post(new MenuPressedEvent());
         }
      };
      //
      drawerLayout.setDrawerListener(mDrawerToggle);
      //
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setHomeButtonEnabled(true);
      //
      this.drawerLayout.post(mDrawerToggle::syncState);
   }

   private void setUpMenu() {
      disableRightDrawer();
      if (!ViewUtils.isLandscapeOrientation(this)) {
         enableLeftDrawer();
         mDrawerToggle.setDrawerIndicatorEnabled(true);
      } else {
         disableLeftDrawer();
         mDrawerToggle.setDrawerIndicatorEnabled(false);
      }
   }

   private void initNavDrawer() {
      navigationDrawerPresenter = new NavigationDrawerPresenter();
      inject(navigationDrawerPresenter);
      //
      navigationDrawerPresenter.attachView(navDrawer, rootComponentsProvider.getActiveComponents());
      navigationDrawerPresenter.setOnItemReselected(this::itemReseleted);
      navigationDrawerPresenter.setOnItemSelected(componentDescription -> itemSelected(componentDescription, this.getClass()));
      navigationDrawerPresenter.setOnLogout(this::logout);
   }

   private void initActionBar(ComponentDescription component) {
      setTitle(component.getToolbarTitle());
      setToolbarLogo(component.getToolbarLogo());
   }

   private void itemSelected(ComponentDescription component, Class<? extends Activity> activitySender) {
      if (component.getKey().equals(MessengerActivityModule.MESSENGER)) {
         MessengerActivity.startMessenger(this);
         finishActivityIfNeed(activitySender);
         return;
      }
      if (component.getKey().equals(DtlActivityModule.DTL)) {
         closeLeftDrawer();
         DtlActivity.startDtl(this);
         finishActivityIfNeed(activitySender);
         return;
      }
      if (component.getKey().equals(WalletActivityModule.WALLET)) {
         closeLeftDrawer();
         WalletActivity.startWallet(this);
         finishActivityIfNeed(activitySender);
         return;
      }
      //
      currentComponent = component;
      //
      eventBus.post(new MenuPressedEvent());
      //
      closeLeftDrawer();
      disableRightDrawer();
      makeActionBarGone(component.isSkipGeneralToolbar());
      //
      currentComponent = component;
      openComponent(component);
   }

   private void finishActivityIfNeed(Class<? extends Activity> componentSender) {
      if (this.getClass() != componentSender) this.finish();
   }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (!cropImageDelegate.onActivityResult(requestCode, resultCode, data)) {
         super.onActivityResult(requestCode, resultCode, data);
      }
   }

   private void openComponent(ComponentDescription component) {
      openComponent(component, null);
   }

   private void openComponent(ComponentDescription component, @Nullable Bundle args) {
      initActionBar(component);
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
            .data(args)
            .build());
   }

   private void itemReseleted(ComponentDescription route) {
      closeLeftDrawer();
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

   boolean handleBackPressed() {
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
      Queryable.from(rightDrawerListeners).forEachR(DrawerListener::onDrawerOpened);
   }

   public void closeRightDrawer() {
      drawerLayout.closeDrawer(GravityCompat.END);
      Queryable.from(rightDrawerListeners).forEachR(DrawerListener::onDrawerClosed);
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
   public void onBackPressed() {
      if (!handleBackPressed()) {
         super.onBackPressed();
      }
   }

   @Override
   protected void onTopLevelBackStackPopped() {
      super.onTopLevelBackStackPopped();
      updateCurrentComponentTitle();
   }

   protected void updateCurrentComponentTitle() {
      currentComponent = this.rootComponentsProvider.getComponent(getSupportFragmentManager());
      //
      if (rootComponentsProvider.getActiveComponents().contains(currentComponent)) {
         navigationDrawerPresenter.setCurrentComponent(currentComponent);
         initActionBar(currentComponent);
         makeActionBarGone(currentComponent.isSkipGeneralToolbar());
      }
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
}
