package com.worldventures.dreamtrips.wallet.ui.wizard.finish;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.home.cardlist.CardListPath;

import flow.Flow;
import flow.History;

public class WalletPinIsSetPresenter extends WalletPresenter<WalletPinIsSetPresenter.Screen, Parcelable> {

    public WalletPinIsSetPresenter(Context context, Injector injector) {
        super(context, injector);
    }

    public void goToBack() {
        Flow.get(getContext()).goBack();
    }

    public void goToCardList() {
        Flow.get(getContext())
                .setHistory(History.single(new CardListPath()), Flow.Direction.REPLACE);
    }

    public interface Screen extends WalletScreen {

    }
}
