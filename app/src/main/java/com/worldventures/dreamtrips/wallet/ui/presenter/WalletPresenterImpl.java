package com.worldventures.dreamtrips.wallet.ui.presenter;

import android.content.Context;
import android.os.Parcelable;

import com.messenger.ui.presenter.BaseViewStateMvpPresenter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.WalletScreen;

public abstract class WalletPresenterImpl<V extends WalletScreen, S extends Parcelable>
        extends BaseViewStateMvpPresenter<V, S> implements WalletPresenter<V, S> {
    private Context context;

    private Injector injector;

    public WalletPresenterImpl(Context context, Injector injector) {
        this.context = context;
        this.injector = injector;
    }

    public Context getContext() {
        return context;
    }

    public Injector getInjector() {
        return injector;
    }
}
