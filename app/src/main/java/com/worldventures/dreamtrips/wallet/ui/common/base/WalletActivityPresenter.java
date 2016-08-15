package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.os.Bundle;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.smartcard.action.support.ConnectAction;
import rx.Observable;
import timber.log.Timber;

public class WalletActivityPresenter extends ActivityPresenter<ActivityPresenter.View> {

    @Inject SnappyRepository snappyRepository;
    @Inject SmartCardInteractor interactor;

    public boolean hasSmartCard() {
        List<SmartCard> smartCards = snappyRepository.getSmartCards();
        for (SmartCard card : smartCards) {
            if (card.cardStatus() == SmartCard.CardStatus.ACTIVE) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interactor.getActiveSmartCardPipe().createObservableResult(new GetActiveSmartCardCommand())
                .flatMap(it -> {
                    if (it != null)
                        return interactor.connectActionPipe().createObservable(new ConnectAction("any", "any"));
                    else
                        return Observable.error(new IllegalStateException("Can't connect to smart card"));
                })
                .subscribe(connectAction -> {
                    Timber.i("Success connection to smart card");
                }, throwable -> {
                   //todo handle connection error
                });
    }
}
