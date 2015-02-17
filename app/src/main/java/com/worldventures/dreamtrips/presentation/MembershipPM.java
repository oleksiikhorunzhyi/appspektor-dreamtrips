package com.worldventures.dreamtrips.presentation;

import android.content.Context;

import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.ContentLoader;
import com.techery.spares.loader.LoaderFactory;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;
import com.worldventures.dreamtrips.core.model.Video;
import com.worldventures.dreamtrips.utils.AdobeTrackingHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MembershipPM extends BasePresentation<BasePresentation.View> {

    @Inject
    LoaderFactory loaderFactory;

    @Inject
    Context context;

    @Inject
    SharedServicesApi sp;
    private List<Video> objects;

    public MembershipPM(View view) {
        super(view);
    }

    public void onItemClick(int position) {

    }

    private CollectionController<Object> adapterController;

    public ContentLoader<List<Object>> getAdapterController() {
        return adapterController;
    }


    public void actionEnroll() {
        AdobeTrackingHelper.enroll();
        activityRouter.openEnroll();
    }

    @Override
    public void init() {
        super.init();
        this.adapterController = loaderFactory.create(0, (context, params) -> {
            this.objects = this.sp.getVideos();
            ArrayList<Object> result = new ArrayList<>();
            result.addAll(objects);
            return result;
        });
    }
}
