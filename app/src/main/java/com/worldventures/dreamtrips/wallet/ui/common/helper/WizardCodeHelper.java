package com.worldventures.dreamtrips.wallet.ui.common.helper;

import android.content.Context;

import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.DelayedSuccessScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.card_alias.WizardCardAliasPath;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public final class WizardCodeHelper {
    public static final long DIALOG_DELAY = 1500L;

    private Context context;
    private WizardInteractor wizardInteractor;

    @Inject
    public WizardCodeHelper(@ForActivity Context context,
                            WizardInteractor wizardInteractor) {
        this.context = context;
        this.wizardInteractor = wizardInteractor;
    }

    public void createAndConnect(DelayedSuccessScreen view,
                                 String code,
                                 Observable.Transformer<ActionState<CreateAndConnectToCardCommand>, ActionState<CreateAndConnectToCardCommand>> bindComposer) {
        wizardInteractor.createAndConnectActionPipe()
                .createObservable(new CreateAndConnectToCardCommand(code))
                .compose(bindComposer)
                .subscribe(new ActionStateSubscriber<CreateAndConnectToCardCommand>()
                        .onStart(createAndConnectToCardCommand -> view.showProgress())
                        .onSuccess(createAndConnectToCardCommand -> {
                            view.hideProgress();
                            view.showSuccessWithDelay(() -> Flow.get(context).set(new WizardCardAliasPath()), DIALOG_DELAY);
                        })
                        .onFail((createAndConnectToCardCommand, throwable) -> {
                            view.hideProgress();
                            view.notifyError(throwable);
                        }));
    }
}
