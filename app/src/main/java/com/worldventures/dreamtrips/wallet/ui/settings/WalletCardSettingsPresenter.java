package com.worldventures.dreamtrips.wallet.ui.settings;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.WizardPinSetupPath;

import javax.inject.Inject;

import flow.Flow;
import rx.Observable;

public class WalletCardSettingsPresenter extends WalletPresenter<WalletCardSettingsPresenter.Screen, Parcelable> {

    @Inject SmartCardInteractor smartCardInteractor;

    private SmartCard smartCard;

    public WalletCardSettingsPresenter(Context context, Injector injector, SmartCard smartCard) {
        super(context, injector);
        this.smartCard = smartCard;
    }

    @Override
    public void attachView(Screen view) {
        super.attachView(view);
        bindSmartCard(smartCard);

        observeSmartCardChanges();
        observeStealthModeController(view);
    }

    private void observeSmartCardChanges() {
        smartCardInteractor.smartCardModifierPipe()
                .observeSuccess()
                .compose(bindViewIoToMainComposer())
                .subscribe(command -> bindSmartCard(this.smartCard = command.smartCard()));
    }

    private void observeStealthModeController(Screen view) {
        view.stealthModeStatus()
                .compose(bindView())
                .skip(1)
                .filter(checkedFlag -> smartCard.stealthMode() != checkedFlag)
                .subscribe(this::stealthModeChanged);
    }

    private void bindSmartCard(SmartCard smartCard) {
        getView().stealthModeStatus(smartCard.stealthMode());
    }

    public void goBack() {
        Flow.get(getContext()).goBack();
    }

    public void resetPin() {
        Flow.get(getContext()).set(new WizardPinSetupPath(smartCard, WizardPinSetupPath.Action.RESET));
    }

    private void stealthModeChanged(boolean isEnabled) {
        smartCardInteractor.setStealthModePipe()
                .createObservable(new SetStealthModeCommand(isEnabled))
                .compose(bindViewIoToMainComposer())
                .subscribe(OperationSubscriberWrapper.<SetStealthModeCommand>forView(getView().provideOperationDelegate())
                        .onFail(getContext().getString(R.string.error_something_went_wrong))
                        .onSuccess(getSuccessMessage(isEnabled), action -> {})
                        .wrap()
                );
    }

    private String getSuccessMessage(boolean isEnabled) {
        return isEnabled ? getContext().getString(R.string.wallet_card_settings_stealth_mode_on)
                : getContext().getString(R.string.wallet_card_settings_stealth_mode_off);
    }

    public interface Screen extends WalletScreen {

        void stealthModeStatus(boolean isEnabled);

        Observable<Boolean> stealthModeStatus();
    }
}
