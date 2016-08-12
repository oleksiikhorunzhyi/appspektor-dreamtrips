package com.worldventures.dreamtrips.wallet.ui.wizard.finish;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.CardListPath;

import javax.inject.Inject;

import flow.Flow;
import flow.History;

public class WalletPinIsSetPresenter extends WalletPresenter<WalletPinIsSetPresenter.Screen, Parcelable> {

    @Inject WizardInteractor wizardInteractor;

    private final SmartCard smartCard;

    public WalletPinIsSetPresenter(Context context, Injector injector, SmartCard smartCard) {
        super(context, injector);
        this.smartCard = smartCard;
    }

    @Override
    public void attachView(Screen view) {
        super.attachView(view);
        wizardInteractor.activateSmartCardPipe()
                .observe()
                .compose(bindViewIoToMainComposer())
                .subscribe(OperationSubscriberWrapper.<ActivateSmartCardCommand>forView(view.provideOperationDelegate())
                        .onSuccess(command -> navigateToDashboardScreen())
                        .onFail(getContext().getString(R.string.error_something_went_wrong))
                        .wrap()
                );
    }

    public void goBack() {
        Flow.get(getContext()).goBack();
    }

    public void activateSmartCard() {
        wizardInteractor.activateSmartCardPipe().send(new ActivateSmartCardCommand(smartCard));
    }

    private void navigateToDashboardScreen() {
        Flow.get(getContext())
                .setHistory(History.single(new CardListPath()), Flow.Direction.REPLACE);
    }

    public interface Screen extends WalletScreen {

    }
}
