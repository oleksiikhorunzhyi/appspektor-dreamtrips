package com.worldventures.dreamtrips.modules.infopages.presenter;

import android.webkit.WebViewClient;

import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.auth.api.command.LoginCommand;
import com.worldventures.dreamtrips.modules.auth.service.LoginInteractor;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action0;
import timber.log.Timber;

public class AuthorizedStaticInfoPresenter extends WebViewFragmentPresenter<AuthorizedStaticInfoPresenter.View> {

    public static final int LIFE_DURATION = 30; // mins

    @Inject LoginInteractor loginInteractor;

    public AuthorizedStaticInfoPresenter(String url) {
        super(url);
    }

    @Override
    public void load() {
        doWithAuth(super::load);
    }

    @Override
    protected void reload() {
        doWithAuth(super::reload);
    }

    private void doWithAuth(Action0 action) {
        UserSession userSession = appSessionHolder.get().get();
        if (userSession.getLastUpdate() > System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(LIFE_DURATION)) {
            action.call();
        } else {
            view.setRefreshing(true);
            view.bindUntilDropView(loginInteractor.loginActionPipe()
                    .createObservable(new LoginCommand())
                    .compose(new IoToMainComposer<>()))
                    .subscribe(new ActionStateSubscriber<LoginCommand>()
                            .onSuccess(loginCommand -> {
                                view.setRefreshing(false);
                                reload();
                            }).onFail((loginCommand, throwable) -> {
                                Timber.e(throwable, "Can't login during WebView loading");
                                view.showError(WebViewClient.ERROR_AUTHENTICATION);
                                view.setRefreshing(false);
                            }));
        }
    }

    public interface View extends WebViewFragmentPresenter.View {
    }
}
