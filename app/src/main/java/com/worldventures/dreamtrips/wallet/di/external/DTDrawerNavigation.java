package com.worldventures.dreamtrips.wallet.di.external;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.core.component.ComponentDescription;
import com.worldventures.core.component.RootComponentsProvider;
import com.worldventures.core.ui.util.ViewUtils;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerViewImpl;
import com.worldventures.dreamtrips.wallet.di.SmartCardModule;
import com.worldventures.dreamtrips.wallet.ui.common.WalletNavigationDelegate;

import rx.functions.Action0;

class DTDrawerNavigation implements WalletNavigationDelegate {

   private final NavigationDrawerPresenter navigationDrawerPresenter;
   private final RootComponentsProvider rootComponentsProvider;
   private final ActivityRouter activityRouter;
   private final Activity activity;

   private DrawerLayout drawerLayout;
   private NavigationDrawerViewImpl navDrawer;
   @Nullable private Action0 logoutAction;

   DTDrawerNavigation(NavigationDrawerPresenter navigationDrawerPresenter, RootComponentsProvider rootComponentsProvider,
         ActivityRouter activityRouter, Activity activity) {
      this.navigationDrawerPresenter = navigationDrawerPresenter;
      this.rootComponentsProvider = rootComponentsProvider;
      this.activityRouter = activityRouter;
      this.activity = activity;
   }

   @Override
   public void init(View view) {
      drawerLayout = view.findViewById(R.id.drawer);
      navDrawer = view.findViewById(R.id.drawer_layout);

      initNavDrawer();
   }

   @Override
   public void setOnLogoutAction(@Nullable Action0 action) {
      this.logoutAction = action;
   }

   @Override
   public void openDrawer() {
      navigationDrawerPresenter.openDrawer();
   }

   private void initNavDrawer() {
      navigationDrawerPresenter.attachView(drawerLayout, navDrawer, rootComponentsProvider.getActiveComponents());
      navigationDrawerPresenter.setOnItemReselected(this::itemReselected);
      navigationDrawerPresenter.setOnItemSelected(this::itemSelected);
      navigationDrawerPresenter.setOnLogout(this::logout);

      navigationDrawerPresenter.setCurrentComponent(rootComponentsProvider.getComponentByKey(SmartCardModule.WALLET));
   }

   private void itemReselected(ComponentDescription route) {
      if (!ViewUtils.isLandscapeOrientation(activity)) {
         drawerLayout.closeDrawer(GravityCompat.START);
      }
   }

   private void itemSelected(ComponentDescription component) {
      activityRouter.openMainWithComponent(component.getKey());
   }

   private void logout() {
      new MaterialDialog.Builder(activity)
            .title(R.string.logout_dialog_title)
            .content(R.string.logout_dialog_message)
            .positiveText(R.string.logout_dialog_positive_btn)
            .negativeText(R.string.logout_dialog_negative_btn)
            .positiveColorRes(R.color.theme_main_darker)
            .negativeColorRes(R.color.theme_main_darker)
            .onPositive((materialDialog, dialogAction) -> {
               if (logoutAction != null) logoutAction.call();
            })
            .show();
   }
}
