package com.worldventures.dreamtrips.wallet.ui.wizard.pin;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.GetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupPinCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.ActionStateSubscriberProgressWrapper;
import com.worldventures.dreamtrips.wallet.ui.wizard.finish.WalletPinIsSetPath;

import javax.inject.Inject;

import flow.Flow;
import timber.log.Timber;

public class WizardPinSetupPresenter extends WalletPresenter<WizardPinSetupPresenter.Screen, Parcelable> {

    private final String smartCardId;

    @Inject
    SmartCardInteractor smartCardInteractor;

    public WizardPinSetupPresenter(Context context, Injector injector, String smartCardId) {
        super(context, injector);
        this.smartCardId = smartCardId;
    }

    public void goToBack() {
        Flow.get(getContext()).goBack();
    }

    @Override
    public void attachView(Screen view) {
        super.attachView(view);
        smartCardInteractor.getSmartCardPipe()
                .createObservableResult(new GetSmartCardCommand(smartCardId))
                .compose(bindViewIoToMainComposer())
                .subscribe(command -> getView().setUserAvatar(command.getResult().getUserPhoto()),
                        throwable -> Timber.e(throwable, ""));
    }

    public void setupPIN() {
        smartCardInteractor.setupPinPipe()
                .createObservable(new SetupPinCommand(smartCardId))
                .compose(bindViewIoToMainComposer())
                .subscribe(ActionStateSubscriberProgressWrapper.<SetupPinCommand>forView(getView().provideOperationDelegate())
                        .onStart(getContext().getString(R.string.wallet_wizard_setup_progress))
                        .onSuccess(setupPinCommand -> Flow.get(getContext()).set(new WalletPinIsSetPath()))
                        .onFail(getContext().getString(R.string.wallet_wizard_setup_error))
                        .wrap());
    }

    public interface Screen extends WalletScreen {
        void setUserAvatar(@Nullable String fileUri);
    }
}