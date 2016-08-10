package com.worldventures.dreamtrips.wallet.ui.common.helper;

import android.content.Context;

import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper.MessageActionHolder;
import com.worldventures.dreamtrips.wallet.ui.wizard.card_alias.WizardCardNamePath;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.ActionState;
import rx.Observable;
import timber.log.Timber;

public final class WizardCodeHelper {
    private Context context;
    private WizardInteractor wizardInteractor;

    @Inject
    public WizardCodeHelper(@ForActivity Context context,
                            WizardInteractor wizardInteractor) {
        this.context = context;
        this.wizardInteractor = wizardInteractor;
    }

    public void createAndConnect(OperationScreen view,
                                 String code,
                                 Observable.Transformer<ActionState<CreateAndConnectToCardCommand>, ActionState<CreateAndConnectToCardCommand>> bindComposer) {
        wizardInteractor.createAndConnectActionPipe()
                .createObservable(new CreateAndConnectToCardCommand(code))
                .compose(bindComposer)
                .subscribe(OperationSubscriberWrapper.<CreateAndConnectToCardCommand>forView(view)
                        .onStart(context.getString(R.string.waller_wizard_scan_barcode_progress_label))
                        .onSuccess(context.getString(R.string.wallet_got_it_label),
                                command -> Flow.get(context).set(new WizardCardNamePath(command.getCode())))
                        .onFail(throwable -> new MessageActionHolder<>(context.getString(R.string.wallet_wizard_scid_validation_error),
                                command -> Timber.e("Could not connect to device")))
                        .wrap());
    }
}