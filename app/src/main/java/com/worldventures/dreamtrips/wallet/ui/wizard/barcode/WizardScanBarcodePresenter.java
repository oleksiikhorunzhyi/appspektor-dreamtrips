package com.worldventures.dreamtrips.wallet.ui.wizard.barcode;

import android.content.Context;
import android.os.Parcelable;

import com.messenger.ui.presenter.ViewStateMvpPresenter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.permission.PermissionConstants;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.permission.PermissionSubscriber;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;

import javax.inject.Inject;

public class WizardScanBarcodePresenter extends WalletPresenter<WizardScanBarcodePresenter.WizardScanBarcodeScreen, Parcelable>
        implements ViewStateMvpPresenter<WizardScanBarcodePresenter.WizardScanBarcodeScreen, Parcelable> {
    @Inject
    PermissionDispatcher permissionDispatcher;

    public WizardScanBarcodePresenter(Context context, Injector injector) {
        super(context, injector);
    }

    public void requestCamera() {
        permissionDispatcher.requestPermission(PermissionConstants.CAMERA_PERMISSIONS)
                .compose(bindView())
                .subscribe(new PermissionSubscriber()
                        .onPermissionGrantedAction(() -> getView().startCamera())
                        .onPermissionRationaleAction(() -> getView().showRationaleForCamera())
                        .onPermissionDeniedAction(() -> getView().showDeniedForCamera()));
    }

    public void barcodeScanned(String barcode) {

    }

    public void startManualInput() {
        //route to manual input screen
    }

    public interface WizardScanBarcodeScreen extends WalletScreen {
        void startCamera();

        void showRationaleForCamera();

        void showDeniedForCamera();
    }
}
