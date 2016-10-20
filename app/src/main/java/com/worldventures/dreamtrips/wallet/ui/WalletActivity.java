package com.worldventures.dreamtrips.wallet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.messenger.delegate.CropImageDelegate;
import com.messenger.ui.util.avatar.MessengerMediaPickerDelegate;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.flow.activity.FlowActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayoutDelegate;
import com.worldventures.dreamtrips.wallet.di.WalletActivityModule;
import com.worldventures.dreamtrips.wallet.ui.common.base.MediaPickerAdapter;
import com.worldventures.dreamtrips.wallet.ui.common.base.MediaPickerService;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletActivityPresenter;
import com.worldventures.dreamtrips.wallet.ui.start.WalletStartPath;

import javax.inject.Inject;

import butterknife.InjectView;
import flow.History;

import static com.worldventures.dreamtrips.wallet.di.WalletActivityModule.WALLET;

@Layout(R.layout.activity_wallet)
public class WalletActivity extends FlowActivity<WalletActivityPresenter> {
   private MediaPickerAdapter mediaPickerAdapter;

   @InjectView(R.id.wallet_photo_picker) PhotoPickerLayout photoPickerLayout;

   @Inject PhotoPickerLayoutDelegate photoPickerLayoutDelegate;

   @Inject MessengerMediaPickerDelegate messengerMediaPickerDelegate;

   @Inject CropImageDelegate cropImageDelegate;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      initPickerLayout();
      mediaPickerAdapter = new MediaPickerAdapter(messengerMediaPickerDelegate, cropImageDelegate);
      navigationDrawerPresenter.setCurrentComponent(rootComponentsProvider.getComponentByKey(WalletActivityModule.WALLET));
      messengerMediaPickerDelegate.resetPhotoPicker();
      messengerMediaPickerDelegate.register();
   }

   @Override
   public void onDestroy() {
      messengerMediaPickerDelegate.unregister();
      mediaPickerAdapter.destroy();
      super.onDestroy();
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      mediaPickerAdapter.onSaveInstanceState(outState);
   }

   @Override
   protected void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      mediaPickerAdapter.onRestoreInstanceState(savedInstanceState);
   }

   @Override
   protected ComponentDescription getCurrentComponent() {
      return rootComponentsProvider.getComponentByKey(WALLET);
   }

   @Override
   protected History provideDefaultHistory() {
      return History.single(new WalletStartPath());
   }

   @Override
   protected WalletActivityPresenter createPresentationModel(Bundle savedInstanceState) {
      return new WalletActivityPresenter();
   }

   public static void startWallet(Context context) {
      context.startActivity(new Intent(context, WalletActivity.class));
   }

   //TODO photo picker should be fully reworked to fit UI needs
   private void initPickerLayout() {
      inject(photoPickerLayout);
      photoPickerLayoutDelegate.setPhotoPickerLayout(photoPickerLayout);
      photoPickerLayoutDelegate.initPicker(getSupportFragmentManager());
   }

   @Override
   public Object getSystemService(@NonNull String name) {
      if (MediaPickerService.SERVICE_NAME.equals(name)) {
         return mediaPickerAdapter;
      }
      return super.getSystemService(name);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (cropImageDelegate.onActivityResult(requestCode, resultCode, data)) return;
      super.onActivityResult(requestCode, resultCode, data);
   }
}
