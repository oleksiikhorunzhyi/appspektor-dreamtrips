package com.worldventures.dreamtrips.wallet.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.worldventures.core.ui.view.activity.BaseActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.module.LegacyActivityModule;
import com.worldventures.dreamtrips.wallet.di.WalletActivityModule;
import com.worldventures.dreamtrips.wallet.service.WalletCropImageService;
import com.worldventures.dreamtrips.wallet.ui.common.LocationScreenComponent;
import com.worldventures.dreamtrips.wallet.ui.common.WalletNavigationDelegate;
import com.worldventures.dreamtrips.wallet.ui.common.activity.WalletActivityPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.activity.WalletActivityView;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.CoreNavigator;
import com.worldventures.dreamtrips.wallet.ui.start.impl.WalletStartScreenImpl;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;

public class WalletActivity extends BaseActivity implements WalletActivityView {

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
      setContentView(R.layout.activity_wallet);
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
   protected List<Object> getModules() {
      List<Object> modules = super.getModules();
      modules.add(new LegacyActivityModule(this));
      modules.add(new WalletActivityModule());
      return modules;
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

   @Override
   public void openBluetoothSettings() {
      Intent requestBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      this.startActivityForResult(requestBluetoothOn, REQUEST_CODE_BLUETOOTH_ON);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (cropImageService.onActivityResult(requestCode, resultCode, data)) {
         return;
      }
      if (locationSettingsService.onActivityResult(requestCode, resultCode, data)) {
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
}
