package com.worldventures.dreamtrips.wallet.ui.common.base;

import android.content.Context;
import android.os.Parcelable;

import com.messenger.ui.presenter.BaseViewStateMvpPresenter;
import com.messenger.ui.presenter.ViewStateMvpPresenter;
import com.techery.spares.module.Injector;

public abstract class WalletPresenter<V extends WalletScreen, S extends Parcelable>
        extends BaseViewStateMvpPresenter<V, S> implements ViewStateMvpPresenter<V, S> {
    private Context context;

    private Injector injector;

    public WalletPresenter(Context context, Injector injector) {
        this.context = context;
        this.injector = injector;
        injector.inject(this);
    }

    public Context getContext() {
        return context;
    }

    public Injector getInjector() {
        return injector;
    }

    @Override
    public void onNewViewState() {
    }

    @Override
    public void applyViewState() {
    }
}
