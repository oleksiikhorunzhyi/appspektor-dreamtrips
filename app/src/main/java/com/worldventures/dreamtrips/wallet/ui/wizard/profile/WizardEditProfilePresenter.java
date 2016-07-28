package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.StringRes;

import com.messenger.ui.presenter.ViewStateMvpPresenter;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletScreen;

import java.sql.Time;

import flow.Flow;
import rx.Observable;
import timber.log.Timber;

public class WizardEditProfilePresenter extends WalletPresenter<WizardEditProfilePresenter.Screen, Parcelable>
        implements ViewStateMvpPresenter<WizardEditProfilePresenter.Screen, Parcelable> {

    public WizardEditProfilePresenter(Context context, Injector injector) {
        super(context, injector);
    }

    public void goToBack() {
        Flow.get(getContext()).goBack();
    }

    public void doOnNext() {

    }

    public void choosePhoto() {
        getView().showPhotoPicker()
                .compose(bindView())
                .subscribe(path -> Timber.d("Test pick image -> %s", path));
    }

    public interface Screen extends WalletScreen {

        Observable<String> showPhotoPicker();

        void hidePhotoPicker();
    }
}
