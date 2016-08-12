package com.worldventures.dreamtrips.wallet.ui.wizard.magstripe;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard.CardType;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import flow.Flow;

public class WizardMagstripePresenter extends WalletPresenter<WizardMagstripePresenter.Screen, Parcelable> {

    private final CardType cardType;

    public WizardMagstripePresenter(Context context, Injector injector, CardType cardType) {
        super(context, injector);
        this.cardType = cardType;
    }

    public void goBack() {
        Flow.get(getContext()).goBack();
    }

    public interface Screen extends WalletScreen {

    }
}
