package com.worldventures.dreamtrips.modules.infopages.presenter;

import android.content.Context;

import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.ContentLoader;
import com.techery.spares.loader.LoaderFactory;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.model.Video;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MembershipVideosPresenter extends Presenter<Presenter.View> {

    @Inject
    LoaderFactory loaderFactory;

    @Inject
    Context context;

    @Inject
    SharedServicesApi sp;

    private List<Video> objects;
    private CollectionController<Object> adapterController;

    public MembershipVideosPresenter(View view) {
        super(view);
    }

    public void onItemClick(int position) {

    }

    public ContentLoader<List<Object>> getAdapterController() {
        return adapterController;
    }

    public void actionEnroll() {
        TrackingHelper.enroll(getUserId());
        activityRouter.openEnroll();
    }

    @Override
    public void init() {
        super.init();
        TrackingHelper.onMemberShipVideos(getUserId());

        this.adapterController = loaderFactory.create(0, (context, params) -> {
            this.objects = this.sp.getVideos();
            ArrayList<Object> result = new ArrayList<>();
            result.addAll(objects);
            return result;
        });
    }
}
