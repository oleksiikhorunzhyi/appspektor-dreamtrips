package com.worldventures.dreamtrips.wallet.ui.wizard.pin;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.SetupPinCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.DelayedSuccessScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.ProgressErrorScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.finish.WalletPinIsSetPath;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class WizardPinSetupPresenter extends WalletPresenter<WizardPinSetupPresenter.Screen, Parcelable> {

    @Inject
    SmartCardInteractor smartCardInteractor;

    public WizardPinSetupPresenter(Context context, Injector injector) {
        super(context, injector);
    }

    public void goToBack() {
        Flow.get(getContext()).goBack();
    }

    public void setupPIN() {
        smartCardInteractor.setupPinPipe()
                .createObservable(new SetupPinCommand())
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
    }
}
