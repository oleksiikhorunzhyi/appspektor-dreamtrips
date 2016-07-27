package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import android.content.Context;
import android.os.Parcelable;

import com.messenger.ui.presenter.ViewStateMvpPresenter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletScreen;

import flow.Flow;

public class WizardEditProfilePresenter extends WalletPresenter<WizardEditProfilePresenter.Screen, Parcelable>
        implements ViewStateMvpPresenter<WizardEditProfilePresenter.Screen, Parcelable> {

    public WizardEditProfilePresenter(Context context, Injector injector) {
        super(context, injector);
    }

    public void goToBack() {
        Flow.get(getContext()).goBack();
    }

    public void doOnNext() {

    }

    public interface Screen extends WalletScreen {

    }
}
