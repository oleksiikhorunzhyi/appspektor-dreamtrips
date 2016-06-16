package com.worldventures.dreamtrips.modules.common.api.janet.command;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.preference.StaticPageHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.common.api.janet.GetStaticPagesHttpAction;
import com.worldventures.dreamtrips.modules.common.model.AppConfig;
import com.worldventures.dreamtrips.modules.common.model.StaticPageConfig;

import java.util.Locale;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;

@CommandAction
public class StaticPageConfigCommand extends Command<StaticPageConfig> implements InjectableAction {

    @Inject
    StaticPageHolder staticPageHolder;
    @Inject
    Janet janet;
    @Inject
    SessionHolder<UserSession> session;
    @Inject
    LocaleHelper localeHelper;
    ActionPipe<GetStaticPagesHttpAction> configPipe;

    @Override
    protected void run(CommandCallback<StaticPageConfig> callback) throws Throwable {
        configPipe = janet.createPipe(GetStaticPagesHttpAction.class, Schedulers.io());
        Observable.concat(fromCache(), fromNetwork())
                .first(sp -> sp != null)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    private Observable<StaticPageConfig> fromNetwork() {
        Locale locale = localeHelper.getAccountLocale(session.get().get().getUser());

        return configPipe.createObservableResult(new GetStaticPagesHttpAction(getBaseUrl(session), locale.getCountry(), locale.getLanguage()))
                .map(GetStaticPagesHttpAction::getStaticPageConfig);
    }

    private Observable<StaticPageConfig> fromCache() {
        Optional<StaticPageConfig> pageHolder = staticPageHolder.get();
        return Observable.just(pageHolder.isPresent() ? pageHolder.get() : null);
    }

    private String getBaseUrl(SessionHolder<UserSession> session) {
        String baseUrl = BuildConfig.SharedServicesApi;

        Optional<UserSession> userSessionOptional = session.get();

        if (userSessionOptional.isPresent()) {
            AppConfig config = session.get().get().getGlobalConfig();

            if (config != null) {
                baseUrl = config.getUrls().getProduction().getAuthBaseURL();
            }
        }
        return String.format("%s/LandingPageServices.svc/GetWebsiteDocumentsByCountry", baseUrl);
    }
}
