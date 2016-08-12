package com.worldventures.dreamtrips.wallet.ui.dashboard.list;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard.CardType;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper.OperationSubscriberWrapper;
import com.worldventures.dreamtrips.wallet.ui.dashboard.detail.CardDetailsPath;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletCardSettingsPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.magstripe.WizardMagstripePath;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public class CardListScreenPresenter extends WalletPresenter<CardListScreenPresenter.Screen, Parcelable> {

    @Inject SmartCardInteractor smartCardInteractor;

    private SmartCard activeSmartCard;

    public CardListScreenPresenter(Context context, Injector injector) {
        super(context, injector);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        smartCardInteractor
                .cardStacksPipe()
                .createObservable(new CardStacksCommand())
                .debounce(100, TimeUnit.MILLISECONDS)
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<CardStacksCommand>()
                        .onProgress((command, integer) -> getView().showRecordsInfo(command.getCachedList()))
                        .onSuccess(command -> getView().showRecordsInfo(command.getResult()))
                        .onFail((command, throwable) -> {}));

        smartCardInteractor.getActiveSmartCardPipe()
                .createObservableResult(new GetActiveSmartCardCommand())
                .compose(bindViewIoToMainComposer())
                .subscribe(it -> setSmartCard(it.getResult()), e -> {/*todo add UI error*/});

        observeSmartCardChanges();
        observeLockController();
    }

    private void observeLockController() {
        getView().lockStatus()
                .compose(bindView())
                .skip(1)
                .filter(checkedFlag -> activeSmartCard.lock() != checkedFlag)
                .subscribe(this::lockChanged);
    }

    private void lockChanged(boolean isLocked) {
        smartCardInteractor.lockPipe().
                createObservable(new SetLockStateCommand(isLocked))
                .compose(bindViewIoToMainComposer())
                .subscribe(OperationSubscriberWrapper.<SetLockStateCommand>forView(getView().provideOperationDelegate())
                        .onFail(getContext().getString(R.string.error_something_went_wrong))
                        .onSuccess(action -> {})
                        .wrap()
                );
    }

    private void observeSmartCardChanges() {
        smartCardInteractor.smartCardModifierPipe()
                .observeSuccess()
                .compose(bindViewIoToMainComposer())
                .subscribe(command -> setSmartCard(command.smartCard()));
    }

    private void setSmartCard(SmartCard smartCard) {
        activeSmartCard = smartCard;
        getView().showSmartCardInfo(smartCard);
    }

    public void showBankCardDetails(BankCard bankCard) {
        Flow.get(getContext()).set(new CardDetailsPath(bankCard));
    }

    public void goBack() {
        Flow.get(getContext()).goBack();
    }

    public void onSettingsChosen() {
        if (activeSmartCard == null) return;
        Flow.get(getContext()).set(new WalletCardSettingsPath(activeSmartCard));
    }

    public void addCreditCard() {
        Flow.get(getContext()).set(new WizardMagstripePath(CardType.CREDIT));
    }

    public void addDebitCard() {
        Flow.get(getContext()).set(new WizardMagstripePath(CardType.DEBIT));
    }

    public interface Screen extends WalletScreen {
        void showRecordsInfo(List<CardStackViewModel> result);

        void showSmartCardInfo(SmartCard smartCard);

        Observable<Boolean> lockStatus();
    }
}
