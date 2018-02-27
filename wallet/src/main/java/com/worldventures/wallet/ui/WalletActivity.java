package com.worldventures.wallet.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.security.ProviderInstaller;
import com.worldventures.core.ui.view.activity.BaseActivity;
import com.worldventures.wallet.R;
import com.worldventures.wallet.service.WalletCropImageService;
import com.worldventures.wallet.ui.common.LocationScreenComponent;
import com.worldventures.wallet.ui.common.WalletNavigationDelegate;
import com.worldventures.wallet.ui.common.activity.WalletActivityPresenter;
import com.worldventures.wallet.ui.common.activity.WalletActivityView;
import com.worldventures.wallet.ui.common.navigation.CoreNavigator;
import com.worldventures.wallet.ui.start.impl.WalletStartScreenImpl;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;

public abstract class WalletActivity extends BaseActivity
      implements WalletActivityView, ProviderInstaller.ProviderInstallListener {

   private static final int REQUEST_CODE_BLUETOOTH_ON = 0xF045;

   @Inject WalletNavigationDelegate navigationDelegate;
   @Inject WalletCropImageService cropImageService;
   @Inject WalletActivityPresenter presenter;
   @Inject CoreNavigator coreNavigator;

   private final LocationScreenComponent locationSettingsService = new LocationScreenComponent(this);
   private final PublishSubject<Void> onDetachSubject = PublishSubject.create();
   private final PublishSubject<Void> onStopSubject = PublishSubject.create();

   private Router router;

   @Override
   public void setupLayout() {
      setContentView(navigationDelegate.getActivityLayout());
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      presenter.attachView(this);

      final FrameLayout rootContainer = findViewById(R.id.root_container);
      router = Conductor.attachRouter(this, rootContainer, savedInstanceState);
      if (!router.hasRootController()) {
         router.setRoot(RouterTransaction.with(new WalletStartScreenImpl()));
      }

      navigationDelegate.init(findViewById(android.R.id.content));
      navigationDelegate.setOnLogoutAction(() -> presenter.logout());
      ProviderInstaller.installIfNeededAsync(getBaseContext(), this);
   }

   @Override
   protected void onStart() {
      super.onStart();
      presenter.bindToBluetooth(onStopSubject.asObservable());
   }

   @Override
   protected void onStop() {
      onStopSubject.onNext(null);
      super.onStop();
   }

   @Override
   public void onDestroy() {
      onDetachSubject.onNext(null);
      presenter.detachView();
      cropImageService.destroy();
      super.onDestroy();
   }

   @Override
   protected void openLoginActivity() {
      coreNavigator.openLoginActivity();
   }

   @Override
   public Object getSystemService(@NonNull String name) {
      if (WalletCropImageService.SERVICE_NAME.equals(name)) {
         return cropImageService;
      } else if (LocationScreenComponent.Companion.getCOMPONENT_NAME().equals(name)) {
         return locationSettingsService;
      }
      return super.getSystemService(name);
   }

   @Override
   public void openBluetoothSettings() {
      Intent requestBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      this.startActivityForResult(requestBluetoothOn, REQUEST_CODE_BLUETOOTH_ON);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      if (cropImageService.onActivityResult(requestCode, resultCode, data)) {
         return;
      }
      if (locationSettingsService.onActivityResult(requestCode, resultCode)) {
         return;
      }
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

   @Override
   public <T> Observable.Transformer<T, T> bindUntilDetach() {
      return tObservable -> tObservable.takeUntil(onDetachSubject.asObservable());
   }

   @Override
   public void onProviderInstalled() {
   }

   @Override
   public void onProviderInstallFailed(int i, Intent intent) {
      //todo: I think we should turn on offline mode because nxt request will be failed
      Toast.makeText(this, R.string.wallet_common_security_provider_update_failed, Toast.LENGTH_SHORT).show();
      Crashlytics.log("Wallet :: Security provider update is failed. Code is " + i);
   }
}
