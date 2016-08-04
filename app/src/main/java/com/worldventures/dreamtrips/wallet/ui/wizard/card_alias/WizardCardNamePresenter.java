package com.worldventures.dreamtrips.wallet.ui.wizard.card_alias;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetupSmartCardNameCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.DelayedSuccessScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePath;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashPath;

import javax.inject.Inject;

import flow.Flow;
import flow.History;
import io.techery.janet.helper.ActionStateSubscriber;

public class WizardCardNamePresenter extends WalletPresenter<WizardCardNamePresenter.Screen, Parcelable> {

    private static final int DIALOG_DELAY = 2000;
    private final String smartCardId;
    @Inject WizardInteractor wizardInteractor;

    public WizardCardNamePresenter(Context context, Injector injector, String smartCardId) {
        super(context, injector);
        this.smartCardId = smartCardId;
    }

    public void goToBack() {
        Flow.get(getContext())
                .setHistory(History.single(new WizardSplashPath()), Flow.Direction.BACKWARD);
    }

    @Override
    public void attachView(Screen view) {
        super.attachView(view);
        observeSmartCardNamePipe();
    }

    private void observeSmartCardNamePipe() {
        wizardInteractor.setupSmartCardNamePipe()
                .observe()
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<SetupSmartCardNameCommand>()
                        .onStart(command -> getView().showProgress())
                        .onSuccess(command ->
                                getView().showSuccessWithDelay(
                                        () -> Flow.get(getContext()).set(new WizardEditProfilePath(command.getCardId())), DIALOG_DELAY))
                        .onFail((command, throwable) -> getView().notifyError(throwable.getCause()))
                );
    }

    public void setupCardName() {
        wizardInteractor.setupSmartCardNamePipe().send(new SetupSmartCardNameCommand(getView().getCardName().trim(), smartCardId));
    }

    public interface Screen extends WalletScreen, DelayedSuccessScreen {

        @NonNull String getCardName();
    }
}
