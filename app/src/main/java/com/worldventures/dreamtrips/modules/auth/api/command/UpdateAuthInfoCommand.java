package com.worldventures.dreamtrips.modules.auth.api.command;

import android.support.annotation.NonNull;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.CirclesInteractor;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.modules.common.api.janet.command.CirclesCommand;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.SessionAbsentException;
import com.worldventures.dreamtrips.modules.friends.model.Circle;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class UpdateAuthInfoCommand extends Command<Void> implements InjectableAction {

    @Inject SessionHolder<UserSession> appSessionHolder;
    @Inject CirclesInteractor queryCirclesInteractor;
    @Inject FeatureManager featureManager;

    @Override
    protected void run(CommandCallback<Void> callback) throws Throwable {
        ArrayList<Observable<?>> sessionQueries = new ArrayList<>();
        sessionQueries.add(sessionExistObservable());
        sessionQueries.add(circles());

        Observable.zip(sessionQueries, args -> (Void) null)
                .timeout(30, TimeUnit.SECONDS)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    @NonNull
    protected Observable<Boolean> sessionExistObservable() {
        return Observable.just(isUserSessionTokenExist())
                .doOnNext(sessionExists -> {
                    if (!sessionExists) throw new SessionAbsentException();
                });
    }

    private boolean isUserSessionTokenExist() {
        UserSession userSession = appSessionHolder.get().isPresent() ? appSessionHolder.get().get() : null;
        return userSession != null && userSession.getApiToken() != null;
    }

    private Observable<ArrayList<Circle>> circles() {
        return Observable.just(featureManager.available(Feature.SOCIAL))
                .flatMap(featureAvailable -> {
                    if (featureAvailable) {
                        return queryCirclesInteractor.pipe()
                                .createObservableResult(new CirclesCommand())
                                .map(Command::getResult);
                    } else {
                        return Observable.just(new ArrayList<>());
                    }
                });
    }
}
