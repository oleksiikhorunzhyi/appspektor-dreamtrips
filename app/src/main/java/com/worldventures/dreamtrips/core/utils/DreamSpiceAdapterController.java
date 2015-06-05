package com.worldventures.dreamtrips.core.utils;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;

import java.util.ArrayList;

public abstract class DreamSpiceAdapterController<BaseItemClass>
        extends RoboSpiceAdapterController<DreamSpiceManager, BaseItemClass> {

    private DreamSpiceManager.SuccessListener<ArrayList<BaseItemClass>> nextRequestSuccessListener =
            new DreamSpiceManager.SuccessListener<ArrayList<BaseItemClass>>() {
                @Override
                public void onRequestSuccess(ArrayList<BaseItemClass> baseItemClasses) {
                    onNextItemsLoaded(baseItemClasses);
                }
            };

    private DreamSpiceManager.SuccessListener<ArrayList<BaseItemClass>> baseRequestSuccessListener =
            new DreamSpiceManager.SuccessListener<ArrayList<BaseItemClass>>() {
                @Override
                public void onRequestSuccess(ArrayList<BaseItemClass> baseItemClasses) {
                    onRefresh(baseItemClasses);
                }
            };

    private DreamSpiceManager.FailureListener baseRequestFailureListener =
            new DreamSpiceManager.FailureListener() {
                @Override
                public void handleError(SpiceException spiceException) {
                    onFailure(spiceException);
                }
            };

    @Override
    protected void executeNextRequest(SpiceRequest<ArrayList<BaseItemClass>> request) {
        spiceManager.execute(request, nextRequestSuccessListener, baseRequestFailureListener);
    }

    @Override
    protected void executeBaseRequest(SpiceRequest<ArrayList<BaseItemClass>> request) {
        spiceManager.execute(request, baseRequestSuccessListener, baseRequestFailureListener);
    }

}
