package com.worldventures.dreamtrips.modules.video.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.modules.membership.model.MediaHeader;
import com.worldventures.dreamtrips.modules.video.api.MemberVideosRequest;
import com.worldventures.dreamtrips.modules.video.model.Category;
import com.worldventures.dreamtrips.modules.video.model.Video;

import java.util.ArrayList;
import java.util.List;

public class ThreeSixtyVideosPresenter extends PresentationVideosPresenter<ThreeSixtyVideosPresenter.View> {

    @Override
    protected MemberVideosRequest getMemberVideosRequest() {
        return new MemberVideosRequest(DreamTripsApi.TYPE_MEMBER_360);
    }

    @Override
    protected void addCategories(List<Category> categories) {
        currentItems = new ArrayList<>();

        List<Video> recentVideos = new ArrayList<>();
        List<Video> featuredVideos = new ArrayList<>();

        Queryable.from(categories).forEachR(cat -> {
            recentVideos.addAll(Queryable.from(cat.getVideos()).filter(Video::isRecent).toList());
            featuredVideos.addAll(Queryable.from(cat.getVideos()).filter(Video::isFeatured).toList());
        });

        currentItems.add(new MediaHeader(context.getString(R.string.featured_header)));
        currentItems.addAll(featuredVideos);
        currentItems.add(new MediaHeader(context.getString(R.string.recent_header)));
        currentItems.addAll(recentVideos);

        view.setItems(currentItems);
    }

    public interface View extends PresentationVideosPresenter.View {
    }
}
