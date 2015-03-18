package com.techery.spares.adapter;

import com.google.common.collect.Collections2;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

public abstract class RoboSpiceAdapterController<BaseItemClass> {

    private SpiceManager spiceManager;
    private IRoboSpiceAdapter<BaseItemClass> adapter;

    private RequestListener<ArrayList<BaseItemClass>> refreshListener = new RequestListener<ArrayList<BaseItemClass>>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            onFinish(LoadType.RELOAD, null, spiceException);
        }

        @Override
        public void onRequestSuccess(ArrayList<BaseItemClass> baseItemClasses) {
            adapter.clear();
            adapter.addItems(baseItemClasses);
            adapter.notifyDataSetChanged();
            onFinish(LoadType.RELOAD, baseItemClasses, null);

        }
    };
    private RequestListener<ArrayList<BaseItemClass>> requestListener = new RequestListener<ArrayList<BaseItemClass>>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            onFinish(LoadType.APPEND, null, spiceException);
        }

        @Override
        public void onRequestSuccess(ArrayList<BaseItemClass> baseItemClasses) {
            adapter.addItems(baseItemClasses);
            adapter.notifyDataSetChanged();
            onFinish(LoadType.APPEND, baseItemClasses, null);
        }
    };

    public abstract SpiceRequest<ArrayList<BaseItemClass>> getRefreshRequest();

    public SpiceRequest<ArrayList<BaseItemClass>> getNextPageRequest(int currentCount) {
        return null;
    }

    public void onStart(LoadType loadType) {
    }

    public void onFinish(LoadType type, List<BaseItemClass> items, SpiceException spiceException) {
    }

    public void setSpiceManager(SpiceManager spiceManager) {
        this.spiceManager = spiceManager;
    }

    public void setAdapter(IRoboSpiceAdapter<BaseItemClass> adapter) {

        this.adapter = adapter;
    }

    public void reload() {
        onStart(LoadType.RELOAD);
        spiceManager.execute(getRefreshRequest(), refreshListener);
    }

    public void loadNext() {
        onStart(LoadType.APPEND);
        SpiceRequest<ArrayList<BaseItemClass>> nextPageRequest = getNextPageRequest(adapter.getCount());
        if (nextPageRequest != null) {
            spiceManager.execute(nextPageRequest, requestListener);
        }
    }

    public static enum LoadType {
        RELOAD, APPEND;
    }
}