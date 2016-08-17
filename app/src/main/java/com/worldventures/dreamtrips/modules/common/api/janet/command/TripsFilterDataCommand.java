package com.worldventures.dreamtrips.modules.common.api.janet.command;

import android.util.Pair;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.trips.command.GetActivitiesCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetRegionsCommand;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;

@CommandAction
public class TripsFilterDataCommand extends CommandWithError<Pair<List<ActivityModel>, List<RegionModel>>>
        implements InjectableAction {

    @Inject Janet janet;

    @Override
    protected void run(CommandCallback<Pair<List<ActivityModel>, List<RegionModel>>> callback)
            throws Throwable {
        ActionPipe<GetActivitiesCommand> activitiesActionPipe = janet
                .createPipe(GetActivitiesCommand.class, Schedulers.io());
        ActionPipe<GetRegionsCommand> regionsActionPipe = janet
                .createPipe(GetRegionsCommand.class, Schedulers.io());

        Observable<List<ActivityModel>> activitiesObservable = activitiesActionPipe
                .createObservableResult(new GetActivitiesCommand())
                .map(GetActivitiesCommand::getResult);
        Observable<List<RegionModel>> regionsObservable = regionsActionPipe
                .createObservableResult(new GetRegionsCommand())
                .map(GetRegionsCommand::getResult);

        Observable.zip(activitiesObservable, regionsObservable, Pair::new)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    @Override
    public int getFallbackErrorMessage() {
        return R.string.error_failed_to_load_activities;
    }
}
