package com.worldventures.dreamtrips.modules.common.api.janet.command;

import android.support.annotation.NonNull;

import com.messenger.api.UiErrorAction;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.preference.LocalesHolder;
import com.worldventures.dreamtrips.modules.common.api.janet.GetLocalesHttpAction;
import com.worldventures.dreamtrips.modules.common.model.AvailableLocale;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;

@CommandAction
public class LocalesCommand extends Command<List<AvailableLocale>> implements InjectableAction, UiErrorAction {

    @Inject
    Janet janet;
    @Inject
    LocalesHolder localeStorage;

    ActionPipe<GetLocalesHttpAction> localeActionActionPipe;

    @Override
    protected void run(CommandCallback<List<AvailableLocale>> callback) throws Throwable {
        localeActionActionPipe = janet.createPipe(GetLocalesHttpAction.class, Schedulers.io());
        Observable.concat(fromCache(), fromNetwork())
                .first(availableLocales -> availableLocales != null && !availableLocales.isEmpty())
                .subscribe(callback::onSuccess, callback::onFail);
    }

    @NonNull
    protected Observable<List<AvailableLocale>> fromNetwork() {
        return localeActionActionPipe.createObservableResult(new GetLocalesHttpAction())
                .map(GetLocalesHttpAction::getAvailableLocales)
                .doOnNext(this::processLocales);
    }

    @NonNull
    protected Observable<ArrayList<AvailableLocale>> fromCache() {
        Optional<ArrayList<AvailableLocale>> localeHolder = localeStorage.get();
        return Observable.just(localeHolder.isPresent() ? localeHolder.get() : new ArrayList<>());
    }

    private void processLocales(List<AvailableLocale> availableLocales) {
        localeStorage.put(new ArrayList<>(availableLocales));
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_locales;
    }
}
