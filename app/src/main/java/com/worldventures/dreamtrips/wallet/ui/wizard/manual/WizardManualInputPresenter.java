package com.worldventures.dreamtrips.wallet.ui.wizard.manual;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.ErrorScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.ProgressScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.card_alias.WizardCardAliasPath;

import javax.inject.Inject;

import flow.Flow;

public class WizardManualInputPresenter extends WalletPresenter<WizardManualInputPresenter.Screen, Parcelable> {
    @Inject
    WizardInteractor wizardInteractor;

    public WizardManualInputPresenter(Context context, Injector injector) {
        super(context, injector);
    }

    public void checkBarcode(String barcode) {
        //
        Flow.get(getContext()).set(new WizardCardAliasPath());
    }

    public void goBack() {
        Flow.get(getContext()).goBack();
    }

    public interface Screen extends WalletScreen, ProgressScreen, ErrorScreen {
    }
}