package com.worldventures.dreamtrips.wallet.ui.dashboard.list;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.dashboard.detail.CardDetailsPath;
import com.worldventures.dreamtrips.wallet.ui.settings.WalletCardSettingsPath;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class CardListScreenPresenter extends WalletPresenter<CardListScreenPresenter.Screen, Parcelable> {
    @Inject
    SmartCardInteractor smartCardInteractor;

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
                        .onSuccess(command -> onBankCardsReceived(command))
                        .onFail((command, throwable) -> onError(throwable)));

        smartCardInteractor.getActiveSmartCardPipe()
                .createObservableResult(new GetActiveSmartCardCommand())
                .compose(bindViewIoToMainComposer())
                .subscribe(it -> setSmartCard(it.getResult()));
    }

    protected void setSmartCard(SmartCard smartCard) {
        activeSmartCard = smartCard;
        getView().showSmartCardInfo(smartCard);
    }

    protected void onBankCardsReceived(CardStacksCommand command) {
        getView().showRecordsInfo(command.getResult());
    }

    public void showBankCardDetails(BankCard bankCard) {
        Flow.get(getContext()).set(new CardDetailsPath(bankCard));
    }

    public void goToBack() {
        Flow.get(getContext()).goBack();
    }

    private void onError(Throwable throwable) {
        Timber.e(throwable, "");
    }

    public void goToSettings() {
        if (activeSmartCard == null) return;
        Flow.get(getContext()).set(new WalletCardSettingsPath(activeSmartCard));
    }

    public interface Screen extends WalletScreen {
        void showRecordsInfo(List<CardStackViewModel> result);

        void showSmartCardInfo(SmartCard smartCard);
    }
}
