package com.worldventures.dreamtrips.modules.trips.command;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.api.janet.GetActivitiesHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class GetActivitiesCommand extends Command<List<ActivityModel>> implements InjectableAction,
        CachedAction<List<ActivityModel>> {

    @Inject Janet janet;

    List<ActivityModel> cachedResult;

    private boolean isClearCommand;

    public GetActivitiesCommand() {

    }

    private GetActivitiesCommand(boolean isClearCommand) {
        this.isClearCommand = isClearCommand;
    }

    @Override
    public List<ActivityModel> getCacheData() {
        return isClearCommand ? new ArrayList<>() : getResult();
    }

    @Override
    public void onRestore(ActionHolder holder, List<ActivityModel> cache) {
        if (isClearCommand) {
            cachedResult = new ArrayList<>();
        } else {
            cachedResult = cache;
        }
    }

    @Override
    public CacheOptions getCacheOptions() {
        return ImmutableCacheOptions.builder().build();
    }

    @Override
    protected void run(CommandCallback<List<ActivityModel>> callback) throws Throwable {
        if ((cachedResult == null || cachedResult.size() == 0) && !isClearCommand) {
            janet.createPipe(GetActivitiesHttpAction.class, Schedulers.io())
                    .createObservableResult(new GetActivitiesHttpAction())
                    .map(GetActivitiesHttpAction::getActivityModels)
                    .subscribe(callback::onSuccess, callback::onFail);
        } else {
            callback.onSuccess(cachedResult);
        }
    }

    public static GetActivitiesCommand clearMemory() {
        return new GetActivitiesCommand(true);
    }
}
