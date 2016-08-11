package com.worldventures.dreamtrips.wallet.ui.wizard.pin;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletCardSettingsPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.finish.WalletPinIsSetPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.success.WalletSuccessPath;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.smartcard.action.settings.PinSetupFinishedAction;
import io.techery.janet.smartcard.action.settings.StartPinSetupAction;

public class WizardPinSetupPresenter extends WalletPresenter<WizardPinSetupPresenter.Screen, Parcelable> {

    private final SmartCard smartCard;
    private final boolean isResetProcess;

    @Inject WizardInteractor wizardInteractor;

    public WizardPinSetupPresenter(Context context, Injector injector, SmartCard smartCard, boolean isResetProcess) {
        super(context, injector);
        this.smartCard = smartCard;
        this.isResetProcess = isResetProcess;
    }

    public void goToBack() {
        Flow.get(getContext()).goBack();
    }

    @Override
    public void attachView(Screen view) {
        super.attachView(view);
        view.setUserAvatar(smartCard.getUserPhoto());
        observeSetupFinishedPipe();
    }

    private void observeSetupFinishedPipe() {
        wizardInteractor.pinSetupFinishedPipe()
                .observe()
                .compose(bindViewIoToMainComposer())
                .subscribe(OperationSubscriberWrapper.<PinSetupFinishedAction>forView(getView().provideOperationDelegate())
                        .onSuccess(setupPinCommand -> navigateToNextScreen())
                        .onFail(getContext().getString(R.string.wallet_wizard_setup_error))
                        .wrap());
    }

    public void setupPIN() {
        wizardInteractor.startPinSetupPipe()
                .createObservable(new StartPinSetupAction())
                .compose(bindViewIoToMainComposer())
                .subscribe(OperationSubscriberWrapper.<StartPinSetupAction>forView(getView().provideOperationDelegate())
                        .onStart(getContext().getString(R.string.wallet_wizard_setup_progress))
                        .onSuccess(action -> {})
                        .onFail(getContext().getString(R.string.wallet_wizard_setup_error))
                        .wrap());
    }

    private void navigateToNextScreen() {
        Flow.get(getContext()).set(createNextScreenPath());
    }

    public StyledPath createNextScreenPath() {
        return isResetProcess ? new WalletSuccessPath(
                getContext().getString(R.string.wallet_wizard_setup_pin_title),
                getContext().getString(R.string.wallet_done_label),
                getContext().getString(R.string.wallet_wizard_setup_new_pin_success),
                new WalletCardSettingsPath(smartCard)
        )
                : new WalletPinIsSetPath();
    }

    public interface Screen extends WalletScreen {

        void setUserAvatar(@Nullable String fileUri);
    }
}