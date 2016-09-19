package com.worldventures.dreamtrips.wallet.ui.wizard.barcode;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.composer.ActionPipeCacheWiper;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.wizard.manual.WizardManualInputPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.welcome.WizardWelcomePath;

import javax.inject.Inject;

import timber.log.Timber;

public class WizardScanBarcodePresenter extends WalletPresenter<WizardScanBarcodePresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject WizardInteractor wizardInteractor;
   @Inject PermissionDispatcher permissionDispatcher;

   public WizardScanBarcodePresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      wizardInteractor.createAndConnectActionPipe()
            .observeWithReplay()
            .compose(bindViewIoToMainComposer())
            .compose(new ActionPipeCacheWiper<>(wizardInteractor.createAndConnectActionPipe()))
            .subscribe(OperationSubscriberWrapper.<CreateAndConnectToCardCommand>forView(view.provideOperationDelegate())
                  .onStart(getContext().getString(R.string.waller_wizard_scan_barcode_progress_label))
                  .onSuccess(getContext().getString(R.string.wallet_got_it_label),
                        command -> navigator.go(new WizardWelcomePath(command.getCode()))
                  )
                  .onFail(throwable -> new OperationSubscriberWrapper.MessageActionHolder<>(getContext().getString(R.string.wallet_wizard_scid_validation_error),
                        command -> Timber.e("Could not connect to device")))
                  .wrap());
   }

   public void requestCamera() {
      permissionDispatcher.requestPermission(PermissionConstants.CAMERA_PERMISSIONS)
            .compose(bindView())
            .subscribe(new PermissionSubscriber().onPermissionGrantedAction(() -> getView().startCamera())
                  .onPermissionRationaleAction(() -> getView().showRationaleForCamera())
                  .onPermissionDeniedAction(() -> getView().showDeniedForCamera()));
   }

   public void barcodeScanned(String barcode) {
      wizardInteractor.associateCardUserCommandPipe().send(new AssociateCardUserCommand(barcode));
   }

   public void startManualInput() {
      navigator.go(new WizardManualInputPath());
   }


   public void goBack() {
      navigator.goBack();
   }

   public interface Screen extends WalletScreen {
      void startCamera();

      void showRationaleForCamera();

      void showDeniedForCamera();
   }
}
