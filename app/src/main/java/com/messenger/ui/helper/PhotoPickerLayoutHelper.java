package com.messenger.ui.helper;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.feed.event.AttachPhotoEvent;
import com.worldventures.dreamtrips.util.Action;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class PhotoPickerLayoutHelper {

    public static final int REQUESTER_ID = -3;

    @Inject
    @Global
    EventBus eventBus;
    private Action<List<ChosenImage>> imageAction;

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

            if (imageAction != null) {
                imageAction.action(Queryable.from(event.getImages()).toList());
            }
        }
    }

    public void subscribe(Action<List<ChosenImage>> imagesAction) {
        eventBus.register(this);
        this.imageAction = imagesAction;
    }

    public void unsubscribe() {
        eventBus.unregister(this);
    }
}