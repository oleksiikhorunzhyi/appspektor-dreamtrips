package com.worldventures.dreamtrips.modules.video.api;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.modules.video.model.Category;

import java.util.ArrayList;

public class MemberVideosRequest extends Query<ArrayList<Category>> {

    private String type;
    private String locale;

    public MemberVideosRequest(String type, String locale) {
        super((Class<ArrayList<Category>>) new ArrayList<Category>().getClass());
        this.type = type;
        this.locale = locale;
    }


    public MemberVideosRequest(String type) {
        this(type, null);
    }


    @Override
    public ArrayList<Category> loadDataFromNetwork() throws Exception {
        if (locale != null) return getService().getVideos(type, locale);
        return getService().getVideos(type);
    }


    @Override
    public int getErrorMessage() {
        return R.string.error_fail_to_load_videos;
    }
}
