package com.worldventures.dreamtrips.modules.membership.presenter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.FileDownloadSpiceManager;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.HumaneErrorTextFactory;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.membership.command.PodcastCommand;
import com.worldventures.dreamtrips.modules.membership.model.MediaHeader;
import com.worldventures.dreamtrips.modules.membership.model.Podcast;
import com.worldventures.dreamtrips.modules.membership.presenter.interactor.PodcastInteractor;
import com.worldventures.dreamtrips.modules.video.FileCachingDelegate;
import com.worldventures.dreamtrips.modules.video.PublicMediaCachingDelegate;
import com.worldventures.dreamtrips.modules.video.api.DownloadFileListener;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class PodcastsPresenter<T extends PodcastsPresenter.View> extends Presenter<T> {

    @Inject
    protected SnappyRepository db;
    @Inject
    @ForApplication
    protected Injector injector;
    @Inject
    protected FileDownloadSpiceManager fileDownloadSpiceManager;
    @Inject
    protected Janet janet;
    @Inject
    protected Context context;
    @Inject
    protected ActivityRouter activityRouter;
    @Inject
    PodcastInteractor podcastInteractor;

    private static final int PODCAST_PRE_PAGE = 10;

    private ActionPipe<PodcastCommand> podcastsActionPipe;
    private PublicMediaCachingDelegate fileCachingDelegate;

    private List<Podcast> items = new ArrayList<>();

    private boolean loading;
    private boolean noMoreItems;

    @Override
    public void takeView(T view) {
        super.takeView(view);
        fileCachingDelegate = new PublicMediaCachingDelegate(db, context, injector, fileDownloadSpiceManager, Environment.DIRECTORY_PODCASTS);
        fileCachingDelegate.setView(this.view);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!fileDownloadSpiceManager.isStarted()) {
            fileDownloadSpiceManager.start(context);
        }
        podcastsActionPipe = podcastInteractor.pipe();
        reloadPodcasts();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (fileDownloadSpiceManager.isStarted()) {
            fileDownloadSpiceManager.shouldStop();
        }
    }

    public void onDeleteAction(CachedEntity entity) {
        fileCachingDelegate.onDeleteAction(entity);
    }

    public void onCancelAction(CachedEntity entity) {
        fileCachingDelegate.onCancelAction(entity);
    }

    public void scrolled(int totalItemCount, int lastVisible) {
        if (!loading && !noMoreItems && lastVisible == totalItemCount - 1) {
            loading = true;
            loadMore();
        }
    }

    protected void loadMore() {
        if (items.size() > 0) {
            loadPodcasts(items.size() / PODCAST_PRE_PAGE + 1);
        }
    }

    public void reloadPodcasts() {
        items.clear();
        loadPodcasts(1);
    }

    private void loadPodcasts(int page) {
        loading = true;
        view.startLoading();
        subscribeGetPodcasts(page);
    }

    private void subscribeGetPodcasts(int page) {
        podcastsActionPipe.createObservableResult(new PodcastCommand(page, PODCAST_PRE_PAGE))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .flatMap(getPodcastsAction -> Observable.from(getPodcastsAction.getResult()))
                .doOnNext(attachCache())
                .doOnNext(this::attachPodcastDownloadListener)
                .toList()
                .subscribe(
                        this::onPodcastsLoaded,
                        throwable -> {
                            if (view != null)
                                view.informUser(new HumaneErrorTextFactory().create(throwable));
                        });
    }

    @NonNull
    private Action1<Podcast> attachCache() {
        return podcast -> {
            CachedEntity e = db.getDownloadMediaEntity(podcast.getUid());
            podcast.setCacheEntity(e);
        };
    }

    private void attachPodcastDownloadListener(Podcast podcast) {
        CachedEntity entity = podcast.getCacheEntity();
        boolean failed = entity.isFailed();
        boolean inProgress = entity.getProgress() > 0;
        boolean cached = entity.isCached(Environment.DIRECTORY_PODCASTS);
        if (!failed && inProgress && !cached) {
            DownloadFileListener listener = new DownloadFileListener(entity, fileCachingDelegate);
            injector.inject(listener);
            fileDownloadSpiceManager.addListenerIfPending(
                    InputStream.class,
                    entity.getUuid(),
                    listener
            );
        }
    }

    private void onPodcastsLoaded(List<Podcast> podcasts) {
        items.addAll(podcasts);
        updateUi(items);
    }

    protected void updateUi(List<Podcast> podcasts) {
        List<Object> items = new ArrayList<>();
        items.add(new MediaHeader(context.getString(R.string.recently_added)));
        items.addAll(podcasts);

        if (view != null) {
            view.setItems(items);
            view.notifyItemChanged(null);
            view.finishLoading();
        }

        noMoreItems = podcasts.size() < PODCAST_PRE_PAGE;
        loading = false;
    }

    public void downloadPodcast(CachedEntity entity) {
        fileCachingDelegate.downloadFile(entity);
    }

    public void deleteCachedPodcast(CachedEntity entity) {
        fileCachingDelegate.deleteCachedFile(entity);
    }

    public void cancelCachingPodcast(CachedEntity entity) {
        fileCachingDelegate.cancelCachingFile(entity);
    }

    public void play(Podcast podcast) {
        try {
            CachedEntity entity = podcast.getCacheEntity();
            if (entity.isCached(Environment.DIRECTORY_PODCASTS)) {
                String path = CachedEntity.getFileForStorage(Environment.DIRECTORY_PODCASTS, podcast.getFileUrl());
                activityRouter.openDeviceAudioPlayerForFile(path);
            } else {
                String url = podcast.getFileUrl();
                activityRouter.openDeviceAudioPlayerForUrl(url);
            }
        } catch (ActivityNotFoundException e) {
            view.informUser(R.string.audio_app_not_found_exception);
        }
    }

    public interface View extends Presenter.View, FileCachingDelegate.View {
        void startLoading();

        void finishLoading();

        void setItems(List items);
    }
}
