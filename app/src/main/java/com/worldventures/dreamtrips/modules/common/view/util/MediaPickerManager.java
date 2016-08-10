package com.worldventures.dreamtrips.modules.common.view.util;

import com.worldventures.dreamtrips.modules.common.model.MediaAttachment;
import com.worldventures.dreamtrips.modules.common.model.PhotoGalleryModel;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import timber.log.Timber;

public class MediaPickerManager {

    private final Subject<MediaAttachment, MediaAttachment> bus = new SerializedSubject<>(PublishSubject.create());

    public void attach(MediaAttachment mediaAttachment) {
        bus.onNext(mediaAttachment);
    }

    public Observable<MediaAttachment> toObservable() {
        return bus;
    }
}