package com.worldventures.dreamtrips.modules.common.presenter.delegate;

import com.messenger.synchmechanism.MessengerConnector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.api.janet.command.UpdateAuthInfoCommand;
import com.worldventures.dreamtrips.modules.common.delegate.AuthInteractor;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

public class AuthorizedDataManager {

    private final MessengerConnector messengerConnector;
    private final SessionHolder<UserSession> appSessionHolder;
    private final AuthInteractor authInteractor;
    private Observable<UpdateAuthInfoCommand> authObservable;
    private boolean inProgress;

    public AuthorizedDataManager(SessionHolder<UserSession> appSessionHolder, AuthInteractor authInteractor, MessengerConnector messengerConnector) {
        this.appSessionHolder = appSessionHolder;
        this.authInteractor = authInteractor;
        this.messengerConnector = messengerConnector;
    }

    public void updateData(AuthDataSubscriber authDataSubscriber) {
        if (!inProgress) {
            inProgress = true;
            authObservable = authInteractor.pipe().createObservableResult(new UpdateAuthInfoCommand())
                    .observeOn(AndroidSchedulers.mainThread())
                    .share();
            authObservable
                    .subscribe(this::done, this::onFail);
        }
        authObservable.subscribe(authDataSubscriber);
    }

    protected void onFail(Throwable e) {
        inProgress = false;
    }

    private void onSuccess() {
        inProgress = false;
    }

    private void done(UpdateAuthInfoCommand command) {
        if (DreamSpiceManager.isCredentialExist(appSessionHolder)) {
            TrackingHelper.setUserId(Integer.toString(appSessionHolder.get().get().getUser().getId()));
            messengerConnector.connect();
            onSuccess();
        }
    }

    public static class AuthDataSubscriber extends Subscriber<UpdateAuthInfoCommand> {

        private Action0 onStart;
        private Action0 onSuccess;
        private Action1<Throwable> onFail;

        @Override
        public void onCompleted() {

        }

        @Override
        public void onStart() {
            onStart.call();
        }

        @Override
        public void onError(Throwable e) {
            onFail.call(e);
        }

        @Override
        public void onNext(UpdateAuthInfoCommand aVoid) {
            onSuccess.call();
        }

        public AuthDataSubscriber onFail(Action1<Throwable> onError) {
            this.onFail = onError;
            return this;
        }

        public AuthDataSubscriber onSuccess(Action0 onSuccess) {
            this.onSuccess = onSuccess;
            return this;
        }

        public AuthDataSubscriber onStart(Action0 onSuccess) {
            this.onStart = onSuccess;
            return this;
        }
    }

}
