package com.techery.spares.adapter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;

import java.util.ArrayList;
import java.util.List;

public abstract class RoboSpiceAdapterController<BaseItemClass> {

    private DreamSpiceManager spiceManager;
    private IRoboSpiceAdapter<BaseItemClass> adapter;

    public abstract SpiceRequest<ArrayList<BaseItemClass>> getRefreshRequest();

    public SpiceRequest<ArrayList<BaseItemClass>> getNextPageRequest(int currentCount) {
        return null;
    }

    public void onStart(LoadType loadType) {
    }

    public void onFinish(LoadType type, List<BaseItemClass> items, SpiceException spiceException) {
    }

    public void setSpiceManager(DreamSpiceManager spiceManager) {
        this.spiceManager = spiceManager;
    }

    public void setAdapter(IRoboSpiceAdapter<BaseItemClass> adapter) {

        this.adapter = adapter;
    }

    public void reload() {
        onStart(LoadType.RELOAD);
        spiceManager.execute(getRefreshRequest(), (baseItemClasses) -> onRefresh(baseItemClasses),
                (exception) -> onFailure(exception));
    }

    public void loadNext() {
        onStart(LoadType.APPEND);
        SpiceRequest<ArrayList<BaseItemClass>> nextPageRequest = getNextPageRequest(adapter.getCount());
        if (nextPageRequest != null) {
            spiceManager.execute(nextPageRequest, (baseItemClasses) -> onRequestSuccess(baseItemClasses),
                    (exception) -> onFailure(exception));
        }
    }

    private void onFailure(SpiceException spiceException) {
        onFinish(LoadType.APPEND, null, spiceException);
    }

    private void onRefresh(ArrayList<BaseItemClass> baseItemClasses) {
        adapter.clear();
        adapter.addItems(baseItemClasses);
        adapter.notifyDataSetChanged();
        onFinish(LoadType.RELOAD, baseItemClasses, null);

    }

    private void onRequestSuccess(ArrayList<BaseItemClass> baseItemClasses) {
        adapter.addItems(baseItemClasses);
        adapter.notifyDataSetChanged();
        onFinish(LoadType.APPEND, baseItemClasses, null);
    }

    public static enum LoadType {
        RELOAD, APPEND;
    }

}