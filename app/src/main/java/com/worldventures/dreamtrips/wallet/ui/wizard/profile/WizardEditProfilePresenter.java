package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.service.CompressImageInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletScreen;

import java.io.File;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class WizardEditProfilePresenter extends WalletPresenter<WizardEditProfilePresenter.Screen, Parcelable> {

    @Inject
    CompressImageInteractor compressImageInteractor;

    public WizardEditProfilePresenter(Context context, Injector injector) {
        super(context, injector);
    }

    public void goToBack() {
        Flow.get(getContext()).goBack();
    }

    public void doOnNext() {

    }

    public void choosePhoto() {
        getView().choosePhotoAndCrop()
                .compose(bindView())
                .subscribe(this::prepareImage, throwable -> Timber.e(throwable, ""));
    }

    private void prepareImage(String path) {
        compressImageInteractor.getCompressImageForSmartCardCommandPipe()
                .createObservable(new CompressImageForSmartCardCommand(path))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindView())
                .subscribe(new ActionStateSubscriber<CompressImageForSmartCardCommand>()
                .onSuccess(command -> getView().setPreviewPhoto(command.getResult())));
    }

    public interface Screen extends WalletScreen {

        Observable<String> choosePhotoAndCrop();

        void hidePhotoPicker();

        void setPreviewPhoto(File photo);
    }
}
