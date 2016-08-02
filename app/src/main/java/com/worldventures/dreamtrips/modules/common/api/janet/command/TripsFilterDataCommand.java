package com.worldventures.dreamtrips.modules.common.api.janet.command;

import android.util.Pair;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.api.janet.GetActivitiesHttpAction;
import com.worldventures.dreamtrips.modules.common.api.janet.GetRegionsHttpAction;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;

@CommandAction
public class TripsFilterDataCommand extends CommandWithError<Pair<GetActivitiesHttpAction, GetRegionsHttpAction>>
        implements InjectableAction {

    @Inject Janet janet;
    @Inject SnappyRepository db;

    @Override
    protected void run(CommandCallback<Pair<GetActivitiesHttpAction, GetRegionsHttpAction>> callback) {
        ActionPipe<GetActivitiesHttpAction> activitiesActionPipe = janet
                .createPipe(GetActivitiesHttpAction.class, Schedulers.io());
        ActionPipe<GetRegionsHttpAction> regionsActionPipe = janet
                .createPipe(GetRegionsHttpAction.class, Schedulers.io());

        Observable<GetActivitiesHttpAction> activitiesObservable = activitiesActionPipe
                .createObservableResult(new GetActivitiesHttpAction());
        Observable<GetRegionsHttpAction> regionsObservable = regionsActionPipe
                .createObservableResult(new GetRegionsHttpAction());

        Observable.zip(activitiesObservable, regionsObservable, Pair::new)
                .doOnNext(pair -> {
                    db.putList(SnappyRepository.ACTIVITIES, pair.first.getActivityModels());
                    db.putList(SnappyRepository.REGIONS, pair.second.getRegionModels());
                }).subscribe(callback::onSuccess, callback::onFail);
    }

    @Override
    public int getFallbackErrorMessage() {
        return R.string.error_fail_to_load_podcast;
    }
}
