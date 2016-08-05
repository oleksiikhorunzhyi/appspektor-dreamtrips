package com.worldventures.dreamtrips.wallet.ui.home.cardlist;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.home.cardlist.util.CardStackViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.card_details.CardDetailsPath;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

public class CardListScreenPresenter extends WalletPresenter<CardListScreenPresenter.Screen, Parcelable> {

    @Inject
    SmartCardInteractor smartCardInteractor;

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
                        .onProgress((command, integer) -> getView().onListReceived(command.getCachedList()))
                        .onSuccess(command -> getView().onListReceived(command.getResult()))
                        .onFail((command, throwable) -> onError(throwable)));
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

    public interface Screen extends WalletScreen {
        void onListReceived(List<CardStackViewModel> result);
    }
}
