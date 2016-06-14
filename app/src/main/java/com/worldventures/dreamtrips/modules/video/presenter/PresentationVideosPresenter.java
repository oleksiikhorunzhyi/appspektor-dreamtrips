package com.worldventures.dreamtrips.modules.video.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.api.DreamTripsApi;
import com.worldventures.dreamtrips.core.api.FileDownloadSpiceManager;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.model.MediaHeader;
import com.worldventures.dreamtrips.modules.video.FileCachingDelegate;
import com.worldventures.dreamtrips.modules.video.api.DownloadFileListener;
import com.worldventures.dreamtrips.modules.video.api.MemberVideosRequest;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;
import com.worldventures.dreamtrips.modules.video.model.Category;
import com.worldventures.dreamtrips.modules.video.model.Video;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PresentationVideosPresenter<T extends PresentationVideosPresenter.View> extends Presenter<T> {

    @Inject
    protected SnappyRepository db;
    @Inject
    @ForApplication
    protected Injector injector;
    @Inject
    protected FileDownloadSpiceManager fileDownloadSpiceManager;

    protected FileCachingDelegate fileCachingDelegate;

    protected List<Object> currentItems;

    protected MemberVideosRequest getMemberVideosRequest() {
        return new MemberVideosRequest(DreamTripsApi.TYPE_MEMBER);
    }

    @Override
    public void takeView(T view) {
        super.takeView(view);
        fileCachingDelegate = new FileCachingDelegate(db, context, injector, fileDownloadSpiceManager);
        fileCachingDelegate.setView(this.view);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!fileDownloadSpiceManager.isStarted()) {
            fileDownloadSpiceManager.start(context);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (fileDownloadSpiceManager.isStarted()) {
            fileDownloadSpiceManager.shouldStop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        view.startLoading();
        loadOnStart();
    }

    public void onDeleteAction(CachedEntity videoEntity) {
        fileCachingDelegate.onDeleteAction(videoEntity);
    }

    public void onCancelAction(CachedEntity cacheEntity) {
        fileCachingDelegate.onCancelAction(cacheEntity);
        TrackingHelper.videoAction(TrackingHelper.ACTION_MEMBERSHIP,
                getAccountUserId(), TrackingHelper.ACTION_MEMBERSHIP_LOAD_CANCELED, cacheEntity.getName());
    }

    public void reload() {
        loadVideos();
    }

    protected void loadOnStart() {
        loadVideos();
    }

    protected void loadVideos() {
        doRequest(getMemberVideosRequest(), categories -> {
            view.finishLoading();
            attachCacheToVideos(categories);
            addCategories(categories);
            attachListeners(categories);
        }, spiceException -> view.finishLoading());
    }

    private void attachCacheToVideos(List<Category> categories) {
        Queryable.from(categories).forEachR(cat -> Queryable.from(cat.getVideos()).forEachR(video -> {
            CachedEntity e = db.getDownloadMediaEntity(video.getUid());
            video.setCacheEntity(e);
        }));
    }

    protected void addCategories(List<Category> categories) {
        currentItems = new ArrayList<>();
        Queryable.from(categories).forEachR(category -> addCategoryHeader(category.getCategory(),
                category.getVideos(), categories.indexOf(category)));
        view.setItems(currentItems);
    }

    protected void addCategoryHeader(String category, List<Video> videos, int categoryIndex) {
        currentItems.add(new MediaHeader(category));
        currentItems.addAll(videos);
    }

    private void attachListeners(List<Category> categories) {
        Queryable.from(categories).forEachR((cat) -> {
            Queryable.from(cat.getVideos()).forEachR(items -> Queryable.from(items).forEachR(item -> {
                CachedEntity cachedVideo = item.getCacheEntity();
                boolean failed = cachedVideo.isFailed();
                boolean inProgress = cachedVideo.getProgress() > 0;
                boolean cached = cachedVideo.isCached(context);
                if (!failed && inProgress && !cached) {
                    DownloadFileListener listener = new DownloadFileListener(cachedVideo, fileCachingDelegate);
                    injector.inject(listener);
                    fileDownloadSpiceManager.addListenerIfPending(
                            InputStream.class,
                            cachedVideo.getUuid(),
                            listener
                    );
                }
            }));
        });
    }

    public void sendAnalytic(String action, String name) {
        TrackingHelper.actionMembershipVideo(action, name);
    }

    public void downloadVideo(CachedEntity cachedEntity) {
        fileCachingDelegate.downloadFile(cachedEntity);
    }

    public void deleteCachedVideo(CachedEntity cachedEntity) {
        fileCachingDelegate.deleteCachedFile(cachedEntity);
    }

    public void cancelCachingVideo(CachedEntity cachedEntity) {
        fileCachingDelegate.cancelCachingFile(cachedEntity);
    }

    public interface View extends Presenter.View, FileCachingDelegate.View {

        void startLoading();

        void finishLoading();

        void setItems(List<Object> videos);
    }
}
