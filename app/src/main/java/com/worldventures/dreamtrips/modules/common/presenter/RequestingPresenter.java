package com.worldventures.dreamtrips.modules.common.presenter;

import com.octo.android.robospice.request.SpiceRequest;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;

public interface RequestingPresenter {
    <T> void doRequest(SpiceRequest<T> request);

    <T> void doRequest(SpiceRequest<T> request,
                                 DreamSpiceManager.SuccessListener<T> successListener);

    <T> void doRequestWithCacheKey(SpiceRequest<T> request, String cacheKey,
                                             DreamSpiceManager.SuccessListener<T> successListener);

    <T> void doRequest(SpiceRequest<T> request,
                                 DreamSpiceManager.SuccessListener<T> successListener,
                                 DreamSpiceManager.FailureListener failureListener);
}