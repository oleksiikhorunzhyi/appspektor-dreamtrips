package com.worldventures.dreamtrips.modules.video.api;

import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;
import com.worldventures.dreamtrips.modules.video.model.Video;

import java.util.ArrayList;

public class MemberVideosRequest extends RetrofitSpiceRequest<ArrayList<Video>, SharedServicesApi> {

    public MemberVideosRequest() {
        super((Class<ArrayList<Video>>) new ArrayList<Video>().getClass(),
                SharedServicesApi.class);
    }

    @Override
    public ArrayList<Video> loadDataFromNetwork() throws Exception {
        return getService().getVideos();
    }
}
