package com.techery.spares.adapter;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

public abstract class RoboSpiceAdapterController<BaseItemClass> {

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
            onFinish(LoadType.RELOAD, null, spiceException);
        }

        @Override
        public void onRequestSuccess(ArrayList<BaseItemClass> baseItemClasses) {
            adapter.addItems(baseItemClasses);
            adapter.notifyDataSetChanged();
            onFinish(LoadType.APPEND, baseItemClasses, null);
        }
    };

    private IRoboSpiceAdapter<BaseItemClass> adapter;

    public abstract SpiceRequest<ArrayList<BaseItemClass>> getRefreshRequest();

    public abstract SpiceRequest<ArrayList<BaseItemClass>> getNextPageRequest(int currentCount);

    public abstract void onStart(LoadType loadType);

    public abstract void onFinish(LoadType type, List<BaseItemClass> items, SpiceException spiceException);

    public void setSpiceManager(SpiceManager spiceManager) {
        this.spiceManager = spiceManager;
    }

    public void setAdapter(IRoboSpiceAdapter<BaseItemClass> adapter) {

        this.adapter = adapter;
    }

    SpiceManager spiceManager;

    public void reload() {
        onStart(LoadType.RELOAD);

        spiceManager.execute(getRefreshRequest(), refreshListener);
    }

    public void loadNext() {
        onStart(LoadType.RELOAD);
        spiceManager.execute(getNextPageRequest(adapter.getCount()), requestListener);
    }

    public static enum LoadType {
        RELOAD, APPEND;
    }
}