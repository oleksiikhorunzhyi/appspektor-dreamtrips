package com.worldventures.dreamtrips.wallet.ui.settings;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.WizardPinSetupPath;

import javax.inject.Inject;

import flow.Flow;
import rx.Observable;
import timber.log.Timber;

public class WalletCardSettingsPresenter extends WalletPresenter<WalletCardSettingsPresenter.Screen, Parcelable> {

    @Inject SmartCardInteractor smartCardInteractor;

    private final SmartCard smartCard;

    public WalletCardSettingsPresenter(Context context, Injector injector, SmartCard smartCard) {
        super(context, injector);
        this.smartCard = smartCard;
    }

    @Override
    public void attachView(Screen view) {
        super.attachView(view);
        Timber.d("Attached card %s", smartCard);
        // TODO: 8/10/16 implement smartCard.hasStealthMode and user below
        view.stealthModeStatus(true);
        view.stealthModeStatus()
                .compose(bindView())
                .skip(1)
                .subscribe(this::stealthModeEnabled);
    }

    public void goBack() {
        Flow.get(getContext()).goBack();
    }

    public void resetPin() {
        Flow.get(getContext()).set(new WizardPinSetupPath(smartCard, WizardPinSetupPath.Action.RESET));
    }

    private void stealthModeEnabled(boolean isEnabled) {
        // TODO: 8/10/16 impl
        Timber.d("Tro-lo-lo-lo %s", isEnabled);
    }

    public interface Screen extends WalletScreen {

        void stealthModeStatus(boolean isEnabled);

        Observable<Boolean> stealthModeStatus();
    }
}
