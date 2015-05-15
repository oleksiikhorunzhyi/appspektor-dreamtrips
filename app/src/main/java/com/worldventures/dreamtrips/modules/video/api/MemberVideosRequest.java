package com.worldventures.dreamtrips.modules.video.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.video.model.Video;

import java.util.ArrayList;

public class MemberVideosRequest extends Query<ArrayList<Video>> {

    private String type;

    public MemberVideosRequest(String type) {
        super((Class<ArrayList<Video>>) new ArrayList<Video>().getClass());
        this.type = type;
    }

    @Override
    public ArrayList<Video> loadDataFromNetwork() throws Exception {
        return getService().getVideos(type);
    }
}
