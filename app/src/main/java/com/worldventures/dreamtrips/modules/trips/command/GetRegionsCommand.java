package com.worldventures.dreamtrips.modules.trips.command;

import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.common.api.janet.GetRegionsHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class GetRegionsCommand extends Command<List<RegionModel>> implements InjectableAction, CachedAction<List<RegionModel>> {

    @Inject Janet janet;

    private List<RegionModel> cachedData;

    private boolean isClearCommand;

    public GetRegionsCommand() {
    }

    private GetRegionsCommand(boolean isClearCommand) {
        this.isClearCommand = isClearCommand;
    }

    @Override
    public List<RegionModel> getCacheData() {
        return isClearCommand ? new ArrayList<>() : getResult();
    }

    @Override
    public void onRestore(ActionHolder holder, List<RegionModel> cache) {
        if (isClearCommand) {
            cachedData = new ArrayList<>();
        } else {
            cachedData = cache;
        }
    }

    @Override
    public CacheOptions getCacheOptions() {
        return ImmutableCacheOptions.builder().build();
    }

    @Override
    protected void run(CommandCallback<List<RegionModel>> callback) throws Throwable {
        if ((cachedData == null || cachedData.size() == 0) && !isClearCommand) {
            janet.createPipe(GetRegionsHttpAction.class, Schedulers.io())
                    .createObservableResult(new GetRegionsHttpAction())
                    .map(GetRegionsHttpAction::getRegionModels)
                    .subscribe(callback::onSuccess, callback::onFail);
        } else {
            callback.onSuccess(cachedData);
        }
    }

    public static GetRegionsCommand clearMemory() {
        return new GetRegionsCommand(true);
    }
}
