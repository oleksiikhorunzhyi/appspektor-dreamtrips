package com.worldventures.dreamtrips.core.utils;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.RoboSpiceAdapterController;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;

import java.util.ArrayList;

public abstract class DreamSpiceAdapterController<BaseItemClass> extends RoboSpiceAdapterController<DreamSpiceManager, BaseItemClass> {

    @Override
    protected void executeNextRequest(SpiceRequest<ArrayList<BaseItemClass>> request) {
        spiceManager.execute(request, new DreamSpiceManager.SuccessListener<ArrayList<BaseItemClass>>() {
            @Override
            public void onRequestSuccess(ArrayList<BaseItemClass> baseItemClasses) {
                DreamSpiceAdapterController.this.onSuccess(baseItemClasses);
            }
        }, new DreamSpiceManager.FailureListener() {
            @Override
            public void handleError(SpiceException spiceException) {
                DreamSpiceAdapterController.this.onFailure(spiceException);
            }
        });
    }

    @Override
    protected void executeBaseRequest(SpiceRequest<ArrayList<BaseItemClass>> request) {
        spiceManager.execute(request, new DreamSpiceManager.SuccessListener<ArrayList<BaseItemClass>>() {
            @Override
            public void onRequestSuccess(ArrayList<BaseItemClass> baseItemClasses) {
                DreamSpiceAdapterController.this.onRefresh(baseItemClasses);
            }
        }, new DreamSpiceManager.FailureListener() {
            @Override
            public void handleError(SpiceException spiceException) {
                DreamSpiceAdapterController.this.onFailure(spiceException);
            }
        });
    }
}
