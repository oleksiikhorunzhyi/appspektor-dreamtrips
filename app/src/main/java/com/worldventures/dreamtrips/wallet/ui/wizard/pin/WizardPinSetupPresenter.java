package com.worldventures.dreamtrips.wallet.ui.wizard.pin;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.GetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupPinCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.ProgressErrorScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.finish.WalletPinIsSetPath;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class WizardPinSetupPresenter extends WalletPresenter<WizardPinSetupPresenter.Screen, Parcelable> {

    private final String smartCardId;

    @Inject SmartCardInteractor smartCardInteractor;

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
                .subscribe(new ActionStateSubscriber<SetupPinCommand>()
                        .onStart(command -> getView().showProgress())
                        .onSuccess(command -> {
                            getView().hideProgress();
                            Flow.get(getContext()).set(new WalletPinIsSetPath());
                        })
                        .onFail((setupPinCommand, throwable) -> {
                            getView().hideProgress();
                            getView().notifyError(throwable);
                        })
                );
    }

    public interface Screen extends WalletScreen, ProgressErrorScreen {

        void setUserAvatar(@Nullable String fileUri);
    }
}
