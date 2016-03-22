package com.messenger.ui.helper;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.feed.event.AttachPhotoEvent;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.subjects.PublishSubject;

public class PhotoPickerDelegate {

    public static final int REQUESTER_ID = -3;

    private EventBus eventBus;

    private PublishSubject<List<ChosenImage>> stream = PublishSubject.create();

    public PhotoPickerDelegate(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void onEvent(AttachPhotoEvent event) {
        if (event.getRequestType() != -1) {
            eventBus.cancelEventDelivery(event);
            eventBus.post(new ImagePickRequestEvent(event.getRequestType(), REQUESTER_ID));
        }
    }

    public void onEvent(ImagePickedEvent event) {
        if (event.getRequesterID() == REQUESTER_ID) {
            eventBus.cancelEventDelivery(event);
            eventBus.removeStickyEvent(event);

            stream.onNext(Queryable.from(event.getImages()).toList());
        }
    }

    public Observable<List<ChosenImage>> watchChosenImages() {
        return stream.asObservable();
    }

    public void register() {
        eventBus.register(this);
    }

    public void unregister() {
        eventBus.unregister(this);
    }
}