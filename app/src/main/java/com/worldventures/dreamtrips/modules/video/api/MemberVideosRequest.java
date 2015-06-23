package com.worldventures.dreamtrips.modules.video.api;

import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.video.model.Video;

import java.util.ArrayList;

public class MemberVideosRequest extends Query<ArrayList<Video>> {

    private String type;
    private String country;

    public MemberVideosRequest(String type, String country) {
        super((Class<ArrayList<Video>>) new ArrayList<Video>().getClass());
        this.type = type;
        this.country = country;
    }

    public MemberVideosRequest(String type) {
        this(type, null);
    }

    @Override
    public ArrayList<Video> loadDataFromNetwork() throws Exception {
        if (country != null) return getService().getVideos(type, country);
        return getService().getVideos(type);
    }
}
