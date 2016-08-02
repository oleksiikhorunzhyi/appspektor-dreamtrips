package com.worldventures.dreamtrips.wallet.ui.wizard.profile;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.wallet.service.SmartCardAvatarInteractor;
import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.LoadImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.wizard.pin.WizardPinSetupPath;

import java.io.File;

import javax.inject.Inject;

import flow.Flow;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class WizardEditProfilePresenter extends WalletPresenter<WizardEditProfilePresenter.Screen, Parcelable> {

    @Inject
    SmartCardAvatarInteractor smartCardAvatarInteractor;
    @Inject
    SessionHolder<UserSession> appSessionHolder;

    public WizardEditProfilePresenter(Context context, Injector injector) {
        super(context, injector);
    }

    @Override
    public void attachView(Screen view) {
        super.attachView(view);
        subscribeSmartCardCommand();

        User userProfile = appSessionHolder.get().get().getUser();
        view.setUserFullName(userProfile.getFullName());
        smartCardAvatarInteractor.getSmartCardAvatarCommandPipe()
                .send(new LoadImageForSmartCardCommand(userProfile.getAvatar().getThumb()));
    }

    public void subscribeSmartCardCommand() {
        smartCardAvatarInteractor.getSmartCardAvatarCommandPipe()
                .observe()
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindView())
                .subscribe(new ActionStateSubscriber<SmartCardAvatarCommand>()
                        .onSuccess(command -> getView().setPreviewPhoto(command.getResult())));
    }

    public void goToBack() {
        Flow.get(getContext()).goBack();
    }

    public void choosePhoto() {
        getView().choosePhotoAndCrop()
                .compose(bindView())
                .subscribe(this::prepareImage, throwable -> Timber.e(throwable, ""));
    }

    private void prepareImage(String path) {
        smartCardAvatarInteractor.getSmartCardAvatarCommandPipe()
                .send(new CompressImageForSmartCardCommand(path));
    }

    public void goNext() {
        Flow.get(getContext()).set(new WizardPinSetupPath());
    }

    public interface Screen extends WalletScreen {

        Observable<String> choosePhotoAndCrop();

        void hidePhotoPicker();

        void setPreviewPhoto(File photo);

        void setUserFullName(String fullName);
    }
}
