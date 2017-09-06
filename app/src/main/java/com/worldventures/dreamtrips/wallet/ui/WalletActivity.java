package com.worldventures.dreamtrips.wallet.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.widget.FrameLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerPresenter;
import com.worldventures.dreamtrips.modules.navdrawer.NavigationDrawerViewImpl;
import com.worldventures.dreamtrips.modules.picker.service.MediaPickerFacebookService;
import com.worldventures.dreamtrips.wallet.di.WalletAppModule;
import com.worldventures.dreamtrips.wallet.service.WalletCropImageService;
import com.worldventures.dreamtrips.wallet.ui.common.LocationScreenComponent;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletActivityPresenter;
import com.worldventures.dreamtrips.wallet.ui.start.impl.WalletStartScreenImpl;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.activity_wallet)
public class WalletActivity extends ActivityWithPresenter<WalletActivityPresenter> implements WalletActivityPresenter.View {

   private static final int REQUEST_CODE_BLUETOOTH_ON = 0xF045;

   @InjectView(R.id.drawer) DrawerLayout drawerLayout;
   @InjectView(R.id.drawer_layout) NavigationDrawerViewImpl navDrawer;
   @InjectView(R.id.root_container) FrameLayout rootContainer;

   @Inject WalletCropImageService cropImageService;
   @Inject MediaPickerFacebookService walletPickerFacebookService;
   @Inject RootComponentsProvider rootComponentsProvider;
   @Inject NavigationDrawerPresenter navigationDrawerPresenter;

   private final LocationScreenComponent locationSettingsService = new LocationScreenComponent(this);

   private Router router;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      router = Conductor.attachRouter(this, rootContainer, savedInstanceState);
      if (!router.hasRootController()) {
         router.setRoot(RouterTransaction.with(new WalletStartScreenImpl()));
      }
      initNavDrawer();
      navigationDrawerPresenter.setCurrentComponent(rootComponentsProvider.getComponentByKey(WalletAppModule.WALLET));
   }

   @Override
   public void onDestroy() {
      cropImageService.destroy();
      super.onDestroy();
   }

   @Override
   protected WalletActivityPresenter createPresentationModel(Bundle savedInstanceState) {
      return new WalletActivityPresenter();
   }

   public static void startWallet(Context context) {
      context.startActivity(new Intent(context, WalletActivity.class));
   }

   @Override
   public Object getSystemService(@NonNull String name) {
      if (WalletCropImageService.SERVICE_NAME.equals(name)) {
         return cropImageService;
      } else if (LocationScreenComponent.COMPONENT_NAME.equals(name)) {
         return locationSettingsService;
      }
      return super.getSystemService(name);
   }

   public void openBluetoothSettings() {
      Intent requestBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      this.startActivityForResult(requestBluetoothOn, REQUEST_CODE_BLUETOOTH_ON);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (cropImageService.onActivityResult(requestCode, resultCode, data)) return;
      if (locationSettingsService.onActivityResult(requestCode, resultCode, data)) return;
      if (walletPickerFacebookService.onActivityResult(requestCode, resultCode, data)) return;
      super.onActivityResult(requestCode, resultCode, data);
   }

   public Router getRouter() {
      return router;
   }

   @Override
   public void onBackPressed() {
      if (!router.handleBack()) {
         super.onBackPressed();
      }
   }

   private void initNavDrawer() {
      navigationDrawerPresenter.attachView(drawerLayout, navDrawer, rootComponentsProvider.getActiveComponents());
      navigationDrawerPresenter.setOnItemReselected(this::itemReselected);
      navigationDrawerPresenter.setOnItemSelected(this::itemSelected);
      navigationDrawerPresenter.setOnLogout(this::logout);
   }

   private void itemSelected(ComponentDescription component) {
      activityRouter.openMainWithComponent(component.getKey());
   }

   private void itemReselected(ComponentDescription route) {
      if (!ViewUtils.isLandscapeOrientation(this)) {
         drawerLayout.closeDrawer(GravityCompat.START);
      }
   }

   private void logout() {
      new MaterialDialog.Builder(this)
            .title(R.string.logout_dialog_title)
            .content(R.string.logout_dialog_message)
            .positiveText(R.string.logout_dialog_positive_btn)
            .negativeText(R.string.logout_dialog_negative_btn)
            .positiveColorRes(R.color.theme_main_darker)
            .negativeColorRes(R.color.theme_main_darker)
            .onPositive((materialDialog, dialogAction) -> getPresentationModel().logout())
            .show();
   }
}
