package com.worldventures.dreamtrips.wallet.ui.wizard.magstripe;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard.CardType;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.card_details.AddCardDetailsPath;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import flow.Flow;
import io.techery.janet.smartcard.model.Record;
import rx.Observable;

public class WizardMagstripePresenter extends WalletPresenter<WizardMagstripePresenter.Screen, Parcelable> {

    private final CardType cardType;

    public WizardMagstripePresenter(Context context, Injector injector, CardType cardType) {
        super(context, injector);
        this.cardType = cardType;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        //// TODO: remove it in future
        Observable.just(null)
                .delay(5, TimeUnit.SECONDS)
                .compose(bindViewIoToMainComposer())
                .subscribe(o -> cardSwiped());
    }

    public void goBack() {
        Flow.get(getContext()).goBack();
    }

    public void cardSwiped() {
        //todo
        Random random = new Random();
        BankCard bankCard = ImmutableBankCard.builder()
                .id(Card.NO_ID)
                .number(Math.abs(random.nextLong()) % 1000000000000000L)
                .type(Record.FinancialService.MASTERCARD)
                .cardType(cardType)
                .expiryMonth(random.nextInt(13))
                .expiryYear(random.nextInt(100))
                .build();
        Flow.get(getContext()).set(new AddCardDetailsPath(bankCard));
    }

    public interface Screen extends WalletScreen {

    }
}
