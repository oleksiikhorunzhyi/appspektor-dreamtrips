package com.worldventures.dreamtrips.modules.trips.service.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.trips.command.GetActivitiesCommand;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;

import java.util.List;

public class ActivitiesStorage implements ActionStorage<List<ActivityModel>> {

    private final SnappyRepository snappyRepository;

    public ActivitiesStorage(SnappyRepository snappyRepository) {
        this.snappyRepository = snappyRepository;
    }

    @Override
    public Class<? extends CachedAction> getActionClass() {
        return GetActivitiesCommand.class;
    }

    @Override
    public void save(@Nullable CacheBundle params, List<ActivityModel> data) {
        snappyRepository.putList(SnappyRepository.ACTIVITIES, data);
    }

    @Override
    public List<ActivityModel> get(@Nullable CacheBundle action) {
        return snappyRepository.readList(SnappyRepository.ACTIVITIES, ActivityModel.class);
    }
}
