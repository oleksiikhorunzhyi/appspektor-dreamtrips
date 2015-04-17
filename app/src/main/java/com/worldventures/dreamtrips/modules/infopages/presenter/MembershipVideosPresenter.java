package com.worldventures.dreamtrips.modules.infopages.presenter;

import android.content.Context;
import android.content.Intent;

import com.techery.spares.loader.CollectionController;
import com.techery.spares.loader.ContentLoader;
import com.techery.spares.loader.LoaderFactory;
import com.worldventures.dreamtrips.core.api.SharedServicesApi;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.model.Video;
import com.worldventures.dreamtrips.modules.video.DownloadVideoService;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.model.DownloadVideoEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MembershipVideosPresenter extends Presenter<Presenter.View> {

    @Inject
    protected LoaderFactory loaderFactory;

    @Inject
    protected SharedServicesApi sp;

    @Inject
    Context context;

    @Inject
    SnappyRepository db;
    private List<Video> objects;
    private CollectionController<Object> adapterController;

    public MembershipVideosPresenter(View view) {
        super(view);
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
            for (Video object : objects) {
                DownloadVideoEntity e = db.getDownloadVideoEntity(String.valueOf(object.getId()));
                object.setEntity(e);
            }
            result.addAll(objects);
            return result;
        });
    }


    public void onEvent(DownloadVideoRequestEvent event) {
        Intent intent = new Intent(context, DownloadVideoService.class);
        DownloadVideoEntity entity = event.getVideo().getDownloadEntity();
        context.startService(intent.putExtra(DownloadVideoService.EXTRA_VIDEO, entity));
    }
}
