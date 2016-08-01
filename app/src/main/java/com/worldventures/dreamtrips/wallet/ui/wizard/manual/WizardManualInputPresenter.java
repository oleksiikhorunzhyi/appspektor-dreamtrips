package com.worldventures.dreamtrips.wallet.ui.wizard.manual;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.DelayedSuccessScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.WizardCodeHelper;

import javax.inject.Inject;

import flow.Flow;

public class WizardManualInputPresenter extends WalletPresenter<WizardManualInputPresenter.Screen, Parcelable> {
    @Inject
    WizardCodeHelper wizardCodeHelper;

    public WizardManualInputPresenter(Context context, Injector injector) {
        super(context, injector);
    }

    public void checkBarcode(String barcode) {
        wizardCodeHelper.createAndConnect(getView(), barcode, bindViewIoToMainComposer());
    }

    public void goBack() {
        Flow.get(getContext()).goBack();
    }

    public interface Screen extends WalletScreen, DelayedSuccessScreen {
    }
}