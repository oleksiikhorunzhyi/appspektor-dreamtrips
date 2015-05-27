package com.techery.spares.adapter;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;

import java.util.ArrayList;
import java.util.List;

public abstract class RoboSpiceAdapterController<T extends SpiceManager, BaseItemClass> {

    protected T spiceManager;
    private IRoboSpiceAdapter<BaseItemClass> adapter;

    public abstract SpiceRequest<ArrayList<BaseItemClass>> getRefreshRequest();

    public SpiceRequest<ArrayList<BaseItemClass>> getNextPageRequest(int currentCount) {
        return null;
    }

    public void onStart(LoadType loadType) {
    }

    public void onFinish(LoadType type, List<BaseItemClass> items, SpiceException spiceException) {
    }

    public void setSpiceManager(T spiceManager) {
        this.spiceManager = spiceManager;
    }

    public void setAdapter(IRoboSpiceAdapter<BaseItemClass> adapter) {
        this.adapter = adapter;
    }

    public boolean hasAdapter() {
        return adapter != null;
    }

    public void reload() {
        onStart(LoadType.RELOAD);
        executeBaseRequest(getRefreshRequest());
    }

    public void loadNext() {
        onStart(LoadType.APPEND);
        SpiceRequest<ArrayList<BaseItemClass>> nextPageRequest = getNextPageRequest(adapter.getCount());
        if (nextPageRequest != null) {
            executeNextRequest(nextPageRequest);
        }
    }

    protected abstract void executeNextRequest(SpiceRequest<ArrayList<BaseItemClass>> request);

    protected abstract void executeBaseRequest(SpiceRequest<ArrayList<BaseItemClass>> request);

    protected void onSuccess(ArrayList<BaseItemClass> baseItemClasses) {
        if (hasAdapter()) {
            adapter.addItems(baseItemClasses);
            adapter.notifyDataSetChanged();
        }
        onFinish(LoadType.APPEND, baseItemClasses, null);
    }

    protected void onFailure(SpiceException spiceException) {
        onFinish(LoadType.APPEND, null, spiceException);
    }

    protected void onRefresh(ArrayList<BaseItemClass> baseItemClasses) {
        if (hasAdapter()) {
            adapter.clear();
            adapter.addItems(baseItemClasses);
            adapter.notifyDataSetChanged();
        }
        onFinish(LoadType.RELOAD, baseItemClasses, null);

    }

    public enum LoadType {
        RELOAD, APPEND
    }

}