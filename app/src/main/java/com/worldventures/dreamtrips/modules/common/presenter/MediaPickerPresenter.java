package com.worldventures.dreamtrips.modules.common.presenter;

import android.util.Pair;

import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.common.model.BasePhotoPickerModel;
import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.common.view.util.Size;
import com.worldventures.dreamtrips.modules.feed.event.PickerDoneEvent;
import com.worldventures.dreamtrips.modules.tripsimages.vision.ImageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.RxThreadFactory;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MediaPickerPresenter extends Presenter<MediaPickerPresenter.View> {

    public static final int REQUESTER_ID = -10;
    private static final String THREAD_NAME_PREFIX = "MEDIA_PICKER_THREAD";

    private int requestId;

    @Inject
    MediaPickerManager mediaPickerManager;
    @Inject
    DrawableUtil drawableUtil;

    private CompositeSubscription compositeSubscription;

    public MediaPickerPresenter(int requestId) {
        this.requestId = requestId;
        compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void dropView() {
        super.dropView();
        if (compositeSubscription.hasSubscriptions() && !compositeSubscription.isUnsubscribed()) {
            compositeSubscription.unsubscribe();
        }
    }

    public void onEvent(ImagePickedEvent event) {
        if (view.isVisibleOnScreen() && event.getRequesterID() == REQUESTER_ID) {
            eventBus.cancelEventDelivery(event);
            eventBus.removeStickyEvent(event);

            compositeSubscription.add(Observable.from(event.getImages())
                    .map(image -> {
                        Pair<String, Size> pair = ImageUtils.generateUri(drawableUtil, image.getFilePathOriginal());
                        return new PhotoGalleryModel(pair.first, pair.second);
                    })
                    .toList()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(photoGalleryModels -> {
                        mediaPickerManager.attach(new MediaAttachment(photoGalleryModels, event.getRequestType(), requestId));
                        eventBus.post(new PickerDoneEvent(new MediaAttachment(photoGalleryModels, event.getRequestType(), requestId)));
                        // need to call back, because this event comes from camera and picker
                        // done method isn't called and picker won't close
                        if (view != null) view.back();
                    }));
        }
    }

    public void attachImages(List<BasePhotoPickerModel> pickedImages, int type) {
        eventBus.post(new PickerDoneEvent());
        //
        compositeSubscription.add(Observable.from(pickedImages)
                .map(element -> {
                    Pair<String, Size> pair = ImageUtils.generateUri(drawableUtil, element.getOriginalPath());
                    return new PhotoGalleryModel(pair.first, pair.second);
                })
                .map(photoGalleryModel -> {
                    ArrayList<PhotoGalleryModel> chosenImages = new ArrayList<>();
                    chosenImages.add(photoGalleryModel);
                    return new MediaAttachment(chosenImages, type, requestId);
                })
                .subscribeOn(Schedulers.from(Executors.newScheduledThreadPool(5, new RxThreadFactory(THREAD_NAME_PREFIX))))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mediaAttachment -> {
                    mediaPickerManager.attach(mediaAttachment);
                }, error -> {
                    Timber.e(error, "");
                }, () -> view.back()));
    }

    public interface View extends Presenter.View {

        boolean back();
    }
}
