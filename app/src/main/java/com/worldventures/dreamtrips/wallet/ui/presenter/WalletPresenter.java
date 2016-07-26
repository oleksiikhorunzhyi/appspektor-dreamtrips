package com.worldventures.dreamtrips.wallet.ui.presenter;

import android.os.Parcelable;

import com.hannesdorfmann.mosby.mvp.MvpView;
import com.messenger.ui.presenter.ViewStateMvpPresenter;

public interface WalletPresenter<V extends MvpView, S extends Parcelable>
        extends ViewStateMvpPresenter<V, S> {
}
