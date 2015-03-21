package com.worldventures.dreamtrips.core.utils.events;

import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;

import java.util.ArrayList;
import java.util.List;

import static com.worldventures.dreamtrips.modules.tripsimages.view.fragment.TripImagesListFragment.Type;

public class FSUploadEvent {
    Type type;
    List<IFullScreenAvailableObject> images = new ArrayList<>();

    private FSUploadEvent(Type type, List<IFullScreenAvailableObject> images) {
        this.type = type;
        this.images = new ArrayList<>(images);
    }

    public static FSUploadEvent create(Type type, List<IFullScreenAvailableObject> images) {
        switch (type) {

            case MEMBER_IMAGES:
                return new MemberImagesFSEvent(type, images);
            case MY_IMAGES:
                return new MyImagesFSEvent(type, images);
            case YOU_SHOULD_BE_HERE:
                return new YSBHImagesFSEvent(type, images);
            case INSPIRE_ME:
                return new InspireMeImagesFSEvent(type, images);
        }
        return null;
    }

    public Type getType() {
        return type;
    }

    public List<IFullScreenAvailableObject> getImages() {
        return images;
    }

    public static class MemberImagesFSEvent extends FSUploadEvent {

        public MemberImagesFSEvent(Type type, List<IFullScreenAvailableObject> images) {
            super(type, images);
        }
    }

    public static class MyImagesFSEvent extends FSUploadEvent {

        public MyImagesFSEvent(Type type, List<IFullScreenAvailableObject> images) {
            super(type, images);
        }
    }

    public static class YSBHImagesFSEvent extends FSUploadEvent {

        public YSBHImagesFSEvent(Type type, List<IFullScreenAvailableObject> images) {
            super(type, images);
        }
    }

    public static class InspireMeImagesFSEvent extends FSUploadEvent {

        public InspireMeImagesFSEvent(Type type, List<IFullScreenAvailableObject> images) {
            super(type, images);
        }
    }
}
