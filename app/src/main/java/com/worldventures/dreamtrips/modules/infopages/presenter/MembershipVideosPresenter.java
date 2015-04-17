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
import com.worldventures.dreamtrips.modules.video.event.DeleteCachedVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.event.DownloadVideoRequestEvent;
import com.worldventures.dreamtrips.modules.video.model.CachedVideo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MembershipVideosPresenter extends Presenter<MembershipVideosPresenter.View> {

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
                CachedVideo e = db.getDownloadVideoEntity(String.valueOf(object.getId()));
                object.setEntity(e);
            }
            result.addAll(objects);
            return result;
        });
    }


    public void onEvent(DownloadVideoRequestEvent event) {
        Intent intent = new Intent(context, DownloadVideoService.class);
        CachedVideo entity = event.getCachedVideo();
        context.startService(intent.putExtra(DownloadVideoService.EXTRA_VIDEO, entity));
    }

    public void onEvent(DeleteCachedVideoRequestEvent event) {
        view.showDeleteDialog(event.getVideoEntity());
    }

    public void onDeleteAction(CachedVideo videoEntity) {
        new File(videoEntity.getFilePath(context)).delete();
        view.notifyAdapter();
    }


    public interface View extends Presenter.View {
        void showDeleteDialog(CachedVideo videoEntity);

        void notifyAdapter();
    }
}
