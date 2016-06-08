package com.worldventures.dreamtrips.modules.common.api.janet.command;

import android.util.Pair;

import com.messenger.api.UiErrorAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.api.janet.GetActivitiesHttpAction;
import com.worldventures.dreamtrips.modules.common.api.janet.GetRegionsHttpAction;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class TripsFilterDataCommand extends Command<Pair<GetActivitiesHttpAction, GetRegionsHttpAction>> implements InjectableAction, UiErrorAction {

    @Inject
    Janet janet;
    @Inject
    SnappyRepository db;

    @Override
    protected void run(CommandCallback<Pair<GetActivitiesHttpAction, GetRegionsHttpAction>> callback) {
        ActionPipe<GetActivitiesHttpAction> activitiesActionPipe = janet.createPipe(GetActivitiesHttpAction.class);
        ActionPipe<GetRegionsHttpAction> regionsActionPipe = janet.createPipe(GetRegionsHttpAction.class);

        Observable<GetActivitiesHttpAction> activitiesObservable = activitiesActionPipe.createObservableResult(new GetActivitiesHttpAction());
        Observable<GetRegionsHttpAction> regionsObservable = regionsActionPipe.createObservableResult(new GetRegionsHttpAction());

        Observable.zip(activitiesObservable, regionsObservable, Pair::new)
                .doOnNext(pair -> {
                    db.putList(SnappyRepository.ACTIVITIES, pair.first.getActivityModels());
                    db.putList(SnappyRepository.REGIONS, pair.second.getRegionModels());
                }).subscribe(callback::onSuccess, callback::onFail);
    }

    @Override
    public int getErrorMessage() {
        return R.string.error_failed_to_load_activities;
    }
}
