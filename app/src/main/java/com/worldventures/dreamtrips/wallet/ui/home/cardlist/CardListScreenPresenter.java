package com.worldventures.dreamtrips.wallet.ui.home.cardlist;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.AttachCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.home.cardlist.util.CardStackViewModel;

import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.smartcard.action.support.ConnectAction;
import io.techery.janet.smartcard.model.Record;
import rx.Subscription;
import timber.log.Timber;

public class CardListScreenPresenter extends WalletPresenter<CardListScreenPresenter.Screen, Parcelable> {

    @Inject SmartCardInteractor smartCardInteractor;
    private Subscription cardsListSubscription;

    public CardListScreenPresenter(Context context, Injector injector) {
        super(context, injector);
    }

    @Override public void onAttachedToWindow() {
        super.onAttachedToWindow();
        temporaryStabSolution();

        cardsListSubscription = smartCardInteractor
                .cardStacksPipe()
                .createObservable(new CardStacksCommand())
                .compose(bindViewIoToMainComposer())
                .subscribe(new ActionStateSubscriber<CardStacksCommand>()
                        .onProgress((command, integer) -> getView().onListReceived(command.getCachedList()))
                        .onSuccess(command -> getView().onListReceived(command.getResult()))
                        .onFail((command, throwable) -> onError(throwable)));
    }

    //be there until add card functionality will be implemented
    protected void temporaryStabSolution() {
        Random random = new Random(System.currentTimeMillis());
        smartCardInteractor.connectActionPipe().createObservable(new ConnectAction("any_memberid", "any_userSecret"))
                .subscribe(connectActionActionState -> {
                }, throwable -> {
                    Timber.e(throwable, "");
                });
        for (int i = 0; i < 8; i++) {
            BankCard bankCard = ImmutableBankCard.builder()
                    .number(Math.abs(random.nextLong()) % 1000000000000000l)
                    .title("Jane's card" + (i + 1))
                    .type(Record.FinancialService.MASTERCARD)
                    .cvv(random.nextInt(1000))
                    .expiryMonth(random.nextInt(13))
                    .expiryYear(random.nextInt(100))
                    .build();

            smartCardInteractor.addRecordPipe().createObservableResult(new AttachCardCommand(bankCard))
                    .subscribe(connectActionActionState -> {
                    }, throwable -> {
                        Timber.e(throwable, "");
                    });
        }
    }

    @Override public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cardsListSubscription.unsubscribe();
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
